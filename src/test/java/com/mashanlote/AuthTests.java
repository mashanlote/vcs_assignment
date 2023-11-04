package com.mashanlote;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.Registration;
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
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Base64;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class AuthTests {

    private final static String ADMIN_AUTH = "Basic " + new String(Base64.encode("timur:password".getBytes()));
    private final static String USER_AUTH = "Basic " + new String(Base64.encode("ainur:drowssap".getBytes()));

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
        when(weatherApiService.getRecentObservations(anyString(), anyInt())).thenReturn(List.of(WeatherObservation.builder().build()));
        when(weatherApiService.getMostRecentObservation(anyString())).thenReturn(WeatherObservation.builder().build());
        when(weatherApiService.getCityList()).thenReturn(List.of(City.builder().build()));
        doNothing().when(weatherApiService).deleteObservation(any(UUID.class));
    }

    @AfterEach
    public void tearDown() {
        h2.stop();
    }

    @Test
    void updateWeatherIfNecessary_TestUnauthenticated() throws Exception {
        mockMvc.perform(
                    post("/weather/cities/Tomsk/update")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateWeatherIfNecessary_TestUnauthorized() throws Exception {
        mockMvc.perform(
                    post("/weather/cities/Tomsk/update")
                            .header("Authorization", USER_AUTH)
                    )
                    .andExpect(status().isForbidden());
    }

    @Test
    void updateWeatherIfNecessary_TestAuthorized() throws Exception {
        mockMvc.perform(
                        post("/weather/cities/Tomsk/update")
                                .header("Authorization", ADMIN_AUTH)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getRecentWeatherObservations_TestUnauthenticated() throws Exception {
        mockMvc.perform(
                        get("/weather/cities/Tomsk/weather")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getRecentWeatherObservations_TestAuthorized() throws Exception {
        mockMvc.perform(
                        get("/weather/cities/Tomsk/weather")
                                .header("Authorization", ADMIN_AUTH)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        get("/weather/cities/Tomsk/weather")
                                .header("Authorization", USER_AUTH)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getCurrentWeather_TestUnauthenticated() throws Exception {
        mockMvc.perform(
                        get("/weather/cities/Tomsk/current")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentWeather_TestAuthorized() throws Exception {
        mockMvc.perform(
                        get("/weather/cities/Tomsk/current")
                                .header("Authorization", ADMIN_AUTH)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        get("/weather/cities/Tomsk/current")
                                .header("Authorization", USER_AUTH)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getCityList_TestUnauthenticated() throws Exception {
        mockMvc.perform(
                        get("/weather/cities")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCityList_TestAuthorized() throws Exception {
        mockMvc.perform(
                        get("/weather/cities")
                                .header("Authorization", ADMIN_AUTH)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        get("/weather/cities")
                                .header("Authorization", USER_AUTH)
                )
                .andExpect(status().isOk());
    }

    @Test
    void deleteWeatherObservation_TestUnauthenticated() throws Exception {
        mockMvc.perform(
                        delete("/weather/observations/85c4f2d9-b06d-49a8-8679-8f7802f0bd2b")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteWeatherObservation_TestUnauthorized() throws Exception {
        mockMvc.perform(
                        delete("/weather/observations/85c4f2d9-b06d-49a8-8679-8f7802f0bd2b")
                                .header("Authorization", USER_AUTH)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteWeatherObservation_TestAuthorized() throws Exception {
        mockMvc.perform(
                        delete("/weather/observations/85c4f2d9-b06d-49a8-8679-8f7802f0bd2b")
                                .header("Authorization", ADMIN_AUTH)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void registerThenAccessEndpointTest() throws Exception {
        var mapper = new ObjectMapper();
        var registration = mapper.writeValueAsString(new Registration("user1", "password1"));
        var encodedUserPassword = "Basic " + new String(Base64.encode("user1:password1".getBytes()));
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registration)
                        )
                        .andExpect(status().isCreated());
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registration)
                )
                .andExpect(status().isConflict());
        mockMvc.perform(
                        get("/weather/cities/Tomsk/weather")
                                .header("Authorization", encodedUserPassword)
                )
                .andExpect(status().isOk());
        mockMvc.perform(
                        post("/weather/cities/Tomsk/update")
                                .header("Authorization", encodedUserPassword)
                )
                .andExpect(status().isForbidden());
    }

}
