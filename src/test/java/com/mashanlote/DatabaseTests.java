package com.mashanlote;

import com.mashanlote.model.exceptions.BadRequestException;
import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.repositories.CityRepository;
import com.mashanlote.services.WeatherApiService;
import jakarta.transaction.Transactional;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;

import static com.mashanlote.controllers.TestData.weatherDTO1;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class DatabaseTests {

    @Container
    public static GenericContainer h2 = new GenericContainer(DockerImageName.parse("oscarfonts/h2"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .waitingFor(Wait.defaultWaitStrategy());

    @Autowired
    private CityRepository cityRepository;
    private WeatherApiService weatherApiService;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        h2.start();
        LiquibaseAutoConfiguration lc = new LiquibaseAutoConfiguration();
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
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.weatherApiService = new WeatherApiService(
                dataSource,
                null,
                null,
                cityRepository,
                jdbcTemplate,
                null,
                null,
                null
        );
    }

    @AfterEach
    public void tearDown() {
        h2.stop();
    }

    @Test
    public void saveWeatherObservationTest_Success() {
        var savedObject = weatherApiService.saveWeatherObservation(weatherDTO1);
        var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM weather_observation WHERE id=?", Integer.class, savedObject.getId());
        Assertions.assertThat(count).isEqualTo(1);
    }

    @Test
    public void saveWeatherObservationTest_Duplicate() {
        var savedObject = weatherApiService.saveWeatherObservation(weatherDTO1);
        Assertions.assertThatThrownBy(() -> weatherApiService.saveWeatherObservation(null))
                .isInstanceOf(BadRequestException.class);
        var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM weather_observation WHERE id=?", Integer.class, savedObject.getId());
        Assertions.assertThat(count).isEqualTo(1);
        var totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM weather_observation", Integer.class);
        Assertions.assertThat(totalCount).isEqualTo(7);
    }

    // Это тест для себя, чтобы убедиться, что база данных создаётся заново каждый раз.
    @Test
    @Transactional
    public void saveWeatherObservationTest_DatabaseIsReinitialized() {
        var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM weather_observation", Integer.class);
        Assertions.assertThat(count).isEqualTo(6);
    }

    @Test
    @Transactional
    public void getRecentObservationTest_Success() {
        var observations = weatherApiService.getRecentObservations("Tomsk", 10);
        Assertions.assertThat(observations.size()).isEqualTo(3);
    }

    @Test
    public void getRecentObservationTest_CityDoesNotExist() {
        Assertions.assertThatThrownBy(
                () -> weatherApiService.getRecentObservations("Omsk", 10)
        ).isInstanceOf(NotFoundException.class);
    }

}
