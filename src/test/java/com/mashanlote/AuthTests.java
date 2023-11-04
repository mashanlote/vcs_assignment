package com.mashanlote;

import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.services.WeatherApiService;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Base64;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class AuthTests {

    private final static String ADMIN_AUTH = "Basic " + new String(Base64.encode("timur:password".getBytes()));

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private WeatherApiService weatherApiService;

    @Container
    public static GenericContainer h2 = new GenericContainer(DockerImageName.parse("oscarfonts/h2"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .waitingFor(Wait.defaultWaitStrategy());


    @BeforeEach
    public void setUp() {
        h2.start();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost:" + h2.getMappedPort(1521) + "/test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        try {
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                            new JdbcConnection(dataSource.getConnection())
                    ));
            liquibase.update("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
        when(weatherApiService.updateWeatherIfNecessary(anyString())).thenReturn(WeatherObservation.builder().build());
    }

    @AfterEach
    public void tearDown() {
        h2.stop();
    }

    @Test
    void updateWeatherIfNecessary_TestUnauthenticated() throws Exception {
        mockMvc.perform(get("/weather/cities/Tomsk/update"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateWeatherIfNecessary_TestUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/weather/cities/Tomsk/update")
                                .header("Authorization", ADMIN_AUTH)
                        )
                        .andExpect(status().isOk());
    }
}
