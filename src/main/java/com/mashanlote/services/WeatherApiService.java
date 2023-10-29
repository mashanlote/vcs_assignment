package com.mashanlote.services;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.exceptions.BadRequestException;
import com.mashanlote.model.exceptions.InternalServerErrorException;
import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.model.weatherapi.WeatherDTO;
import com.mashanlote.repositories.CityRepository;
import com.zaxxer.hikari.HikariDataSource;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class WeatherApiService {

    private final static String FIND_CITY_BY_NAME = "SELECT * FROM city WHERE name=?";
    private final static String FIND_ALL_CITIES = "SELECT * FROM city";
    private final static String FIND_WEATHER_TYPE_BY_ID = "SELECT * FROM weather_type WHERE id=?";
    private final static String FIND_WEATHER_OBSERVATION_BY_CITY_ID_AND_DATE_TIME = "SELECT * FROM weather_observation " +
            "WHERE city_id=? AND date_time=?";
    private final static String CREATE_CITY = "INSERT INTO city (id, name) values (?, ?)";
    private final static String CREATE_WEATHER_TYPE = "INSERT INTO weather_type (id, day_description, night_description) " +
            "values(?, ?, ?)";
    private final static String CREATE_WEATHER_OBSERVATION = "INSERT INTO weather_observation " +
            "(id, city_id, weather_type_id, temperature, date_time) values (?, ?, ?, ?, ?)";
    private final static String DELETE_WEATHER_OBSERVATION_BY_ID = "DELETE FROM weather_observation WHERE id=?";

    private final DataSource dataSource;
    private final String URL;
    private final CityRepository cityRepository;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final PlatformTransactionManager transactionManager;

    @Qualifier("weather")
    RestTemplate weatherApi;

    public WeatherApiService(
            DataSource dataSource,
            RestTemplate weatherApi,
            @Value("${api-key}") String apiKey,
            CityRepository cityRepository,
            JdbcTemplate jdbcTemplate,
            TransactionTemplate transactionTemplate,
            PlatformTransactionManager transactionManager) {
        this.dataSource = dataSource;
        this.weatherApi = weatherApi;
        this.URL = "/current.json?key=" + apiKey + "&q={city}";
        this.cityRepository = cityRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.transactionManager = transactionManager;
    }

    @RateLimiter(name = "api")
    public WeatherObservation fetchWeatherAndStoreInDb(String city) {
        var weather = fetchWeatherFromExternalApi(city);
        return saveWeatherObservation(weather);
    }

    // @VisibleForTesting
    public WeatherDTO fetchWeatherFromExternalApi(String city) {
        return weatherApi.getForObject(URL, WeatherDTO.class, city);
    }

    private WeatherObservation saveWeatherObservation(WeatherDTO weather) {
        if (weather == null) throw new BadRequestException();
        var cityName = weather.location().name();
        var condition = weather.current().condition();
        var temperature = weather.current().temp_c();
        var dateTime = weather.current().last_updated();
        WeatherObservation observation;
        try (Connection conn = dataSource.getConnection()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            conn.setAutoCommit(false);
            try (PreparedStatement findCityStatement = conn.prepareStatement(FIND_CITY_BY_NAME);
                 PreparedStatement findWeatherObservationStatement = conn.prepareStatement(
                         FIND_WEATHER_OBSERVATION_BY_CITY_ID_AND_DATE_TIME);
                 PreparedStatement findWeatherTypeStatement = conn.prepareStatement(FIND_WEATHER_TYPE_BY_ID);
                 PreparedStatement createCityStatement = conn.prepareStatement(CREATE_CITY);
                 PreparedStatement createWeatherTypeStatement = conn.prepareStatement(CREATE_WEATHER_TYPE);
                 PreparedStatement createWeatherObservationStatement = conn.prepareStatement(CREATE_WEATHER_OBSERVATION);
            ) {
                findCityStatement.setString(1, cityName);
                var findCityQueryResult = findCityStatement.executeQuery();
                String cityId;
                if (!findCityQueryResult.isBeforeFirst()) {
                    cityId = UUID.randomUUID().toString();
                    createCityStatement.setString(1, cityId);
                    createCityStatement.setString(2, cityName);
                    createCityStatement.executeUpdate();
                } else {
                    findCityQueryResult.next();
                    cityId = findCityQueryResult.getString("id");
                }
                findWeatherObservationStatement.setString(1, cityId);
                findWeatherObservationStatement.setTimestamp(2, Timestamp.valueOf(weather.current().last_updated()));
                var observations = findWeatherObservationStatement.executeQuery();
                if (observations.isBeforeFirst()) {
                    observations.next();
                    conn.rollback(); // unnecessary
                    return WeatherObservation.builder()
                            .id(UUID.fromString(observations.getString("id")))
                            .city(City.builder()
                                    .name(cityName)
                                    .id(UUID.fromString(cityId))
                                    .build())
                            .weatherType(WeatherType.builder()
                                    .id(condition.code())
                                    .dayDescription(condition.text())
                                    .nightDescription(condition.text())
                                    .build())
                            .temperature(temperature)
                            .dateTime(dateTime)
                            .build();
                }
                findWeatherTypeStatement.setInt(1, condition.code());
                var findWeatherTypeQueryResult = findWeatherTypeStatement.executeQuery();
                if (!findWeatherTypeQueryResult.isBeforeFirst()) {
                    createWeatherTypeStatement.setInt(1, condition.code());
                    createWeatherTypeStatement.setString(2, condition.text());
                    createWeatherTypeStatement.setString(3, condition.text());
                    createWeatherTypeStatement.executeUpdate();
                }
                var obsId = UUID.randomUUID();
                createWeatherObservationStatement.setString(1, obsId.toString());
                createWeatherObservationStatement.setString(2, cityId);
                createWeatherObservationStatement.setInt(3, condition.code());
                createWeatherObservationStatement.setDouble(4, temperature);
                createWeatherObservationStatement.setTimestamp(5, Timestamp.valueOf(dateTime));
                createWeatherObservationStatement.executeUpdate();
                conn.commit();
                observation = WeatherObservation.builder()
                        .id(obsId)
                        .city(City.builder()
                                .name(cityName)
                                .id(UUID.fromString(cityId))
                                .build())
                        .weatherType(WeatherType.builder()
                                .id(condition.code())
                                .dayDescription(condition.text())
                                .nightDescription(condition.text())
                                .build())
                        .temperature(temperature)
                        .dateTime(dateTime)
                        .build();
            } catch (SQLException e) {
                conn.rollback();
                throw new InternalServerErrorException();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        return observation;
    }

    @Transactional
    public List<WeatherObservation> getRecentObservations(String cityName, Integer limit) {
        var city = cityRepository.findByName(cityName);
        if (city.isEmpty()) throw new NotFoundException();
        return city.get().getWeatherObservations().stream()
                .sorted(Comparator.comparing(WeatherObservation::getDateTime))
                .limit(10).toList();
    }

    @Transactional
    public WeatherObservation getMostRecentObservation(String cityName) {
        var city = jdbcTemplate.queryForObject(FIND_CITY_BY_NAME,
                (rs, rn) -> City.builder()
                        .name(rs.getString("name"))
                        .id(UUID.fromString(rs.getString("id")))
                        .build(),
                cityName);
        return jdbcTemplate.queryForObject(
                "SELECT * FROM weather_observation" +
                        " LEFT JOIN weather_type ON weather_observation.weather_type_id = weather_type.id" +
                        " WHERE city_id=?" +
                        " ORDER BY date_time DESC LIMIT 1",
                (rs, rn) -> WeatherObservation.builder()
                        .id(UUID.fromString(rs.getString("id")))
                        .dateTime(rs.getTimestamp("date_time").toLocalDateTime())
                        .temperature(rs.getDouble("temperature"))
                        .city(city)
                        .weatherType(WeatherType.builder()
                                .id(rs.getInt("weather_type.id"))
                                .dayDescription(rs.getString("weather_type.day_description"))
                                .nightDescription(rs.getString("weather_type.night_description"))
                                .build()
                        ).build(),
                city.getId());
    }

    public void deleteObservation(UUID id) {
        transactionTemplate.execute(status -> jdbcTemplate.update(
                DELETE_WEATHER_OBSERVATION_BY_ID,
                id.toString())
        );
    }

    public List<City> getCityList() {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        var transaction = transactionManager.getTransaction(transactionDefinition);
        try {
            var cities = jdbcTemplate.query(FIND_ALL_CITIES,
                    (rs, rn) -> City.builder()
                            .name(rs.getString("name"))
                            .id(UUID.fromString(rs.getString("id")))
                            .build()
            );
            transactionManager.commit(transaction);
            return cities;
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

}
