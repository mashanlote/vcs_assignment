package com.mashanlote.services;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.exceptions.ConflictException;
import com.mashanlote.model.exceptions.InternalServerErrorException;
import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.model.weather.CreateWeatherObservationRequest;
import com.mashanlote.model.weather.UpdateWeatherObservationRequest;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service("JDBC")
public class WeatherServiceJdbc implements WeatherService {

    HikariDataSource dataSource;

    public WeatherServiceJdbc(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UUID createCity(String name) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "INSERT INTO city (id, name) VALUES (?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            UUID id = UUID.randomUUID();
            st.setString(1, id.toString());
            st.setString(2, name);
            st.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public City getCity(UUID id) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "SELECT * FROM city WHERE id=?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, id.toString());
            var rs = st.executeQuery();
            boolean exists = rs.first();
            if (!exists) {
                throw new NotFoundException();
            }
            String name = rs.getString("name");
            return City.builder()
                    .id(id)
                    .name(name)
                    .build();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<City> getCities() {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "SELECT * FROM city";
        try (PreparedStatement st = con.prepareStatement(query)) {
            var rs = st.executeQuery();
            var cities = new ArrayList<City>();
            while (rs.next()) {
                String name = rs.getString("name");
                UUID id = UUID.fromString(rs.getString("id"));
                var city = City.builder().name(name).id(id).build();
                cities.add(city);
            }
            return cities;
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<UUID> getCities(String name) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "SELECT id FROM city WHERE name=?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, name);
            var rs = st.executeQuery();
            var ids = new ArrayList<UUID>();
            while (rs.next()) {
                // Вообще, конечно, нет необходимости постоянно переводить UUID в строку и обратно.
                ids.add(UUID.fromString(rs.getString("id")));
            }
            return ids;
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void updateCity(UUID id, String name) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "UPDATE city SET name=? WHERE id=?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, name);
            st.setString(2, id.toString());
            int updated = st.executeUpdate();
            if (updated == 0) throw new NotFoundException();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void deleteCity(UUID id) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String deleteCityQuery = "DELETE FROM city WHERE id=?";
        String deleteObservationsQuery = "DELETE FROM weather_observation WHERE city_id=?";
        try (PreparedStatement deleteCityStatement = con.prepareStatement(deleteCityQuery);
             PreparedStatement deleteObservationsStatement = con.prepareStatement(deleteObservationsQuery);
        ) {
            con.setAutoCommit(false);
            deleteObservationsStatement.setString(1, id.toString());
            deleteCityStatement.setString(1, id.toString());
            int deletedObservations = deleteObservationsStatement.executeUpdate();
            int deletedCities = deleteCityStatement.executeUpdate();
            if (deletedCities == 0) throw new NotFoundException();
            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new InternalServerErrorException();
            }
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void createOrUpdateWeatherType(WeatherType type) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "MERGE INTO weather_type (id, day_description, night_description) KEY(id) VALUES(?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, type.getId());
            st.setString(2, type.getDayDescription());
            st.setString(3, type.getNightDescription());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public WeatherType getWeatherType(Integer id) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "SELECT * FROM weather_type WHERE id=?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);
            var rs = st.executeQuery();
            boolean exists = rs.first();
            if (!exists) {
                throw new NotFoundException();
            }
            String dayDescription = rs.getString("day_description");
            String nightDescription = rs.getString("night_description");
            return WeatherType.builder()
                    .id(id)
                    .dayDescription(dayDescription)
                    .nightDescription(nightDescription)
                    .build();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<WeatherType> getWeatherTypes() {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "SELECT * FROM weather_type";
        try (PreparedStatement st = con.prepareStatement(query)) {
            var rs = st.executeQuery();
            var weatherTypes = new ArrayList<WeatherType>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String dayDescription = rs.getString("day_description");
                String nightDescription = rs.getString("night_description");
                var type = WeatherType.builder()
                        .id(id)
                        .dayDescription(dayDescription)
                        .nightDescription(nightDescription)
                        .build();
                weatherTypes.add(type);
            }
            return weatherTypes;
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void deleteWeatherType(Integer id) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String deleteWeatherTypeQuery = "DELETE FROM weather_type WHERE id=?";
        String deleteObservationsQuery = "DELETE FROM weather_observation WHERE weather_type_id=?";
        try (PreparedStatement deleteWeatherTypeStatement = con.prepareStatement(deleteWeatherTypeQuery);
             PreparedStatement deleteObservationsStatement = con.prepareStatement(deleteObservationsQuery);
        ) {
            con.setAutoCommit(false);
            deleteObservationsStatement.setInt(1, id);
            deleteWeatherTypeStatement.setInt(1, id);
            int deletedObservations = deleteObservationsStatement.executeUpdate();
            int deletedWeatherTypes = deleteWeatherTypeStatement.executeUpdate();
            if (deletedWeatherTypes == 0) throw new NotFoundException();
            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new InternalServerErrorException();
            }
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void createWeatherObservation(CreateWeatherObservationRequest request) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }

        String checkCityExists = "SELECT id FROM city WHERE id=?";
        String checkNoConflictingObservation = "SELECT id FROM weather_observation WHERE city_id=? AND date_time=?";
        String insertQuery = "INSERT INTO weather_observation (id, city_id, weather_type_id, temperature, date_time) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement checkCityExistsStatement = con.prepareStatement(checkCityExists);
             PreparedStatement checkNoConflict = con.prepareStatement(checkNoConflictingObservation);
             PreparedStatement insertStatement = con.prepareStatement(insertQuery);
        ) {
            checkCityExistsStatement.setString(1, request.cityId().toString());
            var foundCity = checkCityExistsStatement.executeQuery();
            if (!foundCity.isBeforeFirst()) {
                throw new NotFoundException();
            }
            checkNoConflict.setString(1, request.cityId().toString());
            checkNoConflict.setTimestamp(2, Timestamp.valueOf(request.dateTime()));
            var foundObservations = checkNoConflict.executeQuery();
            if (foundObservations.isBeforeFirst()) {
               throw new ConflictException();
            }
            insertStatement.setString(1, UUID.randomUUID().toString());
            insertStatement.setString(2, request.cityId().toString());
            insertStatement.setInt(3, request.weatherTypeId());
            insertStatement.setDouble(4, request.temperature());
            insertStatement.setTimestamp(5, Timestamp.valueOf(request.dateTime()));
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<WeatherObservation> getCityWeatherObservation(UUID cityId) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String getCityQuery = "SELECT * FROM city WHERE id=?";
        String getObservationsQuery = "SELECT * FROM weather_observation WHERE city_id=?";
        String getWeatherTypeQuery = "SELECT * FROM weather_type WHERE id=?";
        try (PreparedStatement getObservationsStatement = con.prepareStatement(getObservationsQuery);
             PreparedStatement getCityStatement = con.prepareStatement(getCityQuery);
             PreparedStatement getWeatherTypeStatement = con.prepareStatement(getWeatherTypeQuery);
        ) {
            getCityStatement.setString(1, cityId.toString());
            var cityFound = getCityStatement.executeQuery();
            if (!cityFound.isBeforeFirst()) {
                throw new NotFoundException();
            }
            cityFound.next();
            var name = cityFound.getString("name");
            var city = City.builder()
                    .name(name)
                    .id(cityId)
                    .build();
            getObservationsStatement.setString(1, cityId.toString());
            var rs = getObservationsStatement.executeQuery();
            var weatherObservations = new ArrayList<WeatherObservation>();
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                Integer weatherTypeId = rs.getInt("weather_type_id");
                Double temperature = rs.getDouble("temperature");
                LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
                getWeatherTypeStatement.setInt(1, weatherTypeId);
                var weatherTypeResultSet = getWeatherTypeStatement.executeQuery();
                weatherTypeResultSet.next();
                String dayDescription = weatherTypeResultSet.getString("day_description");
                String nightDescription = weatherTypeResultSet.getString("night_description");
                var weatherType = WeatherType.builder()
                        .id(weatherTypeId)
                        .dayDescription(dayDescription)
                        .nightDescription(nightDescription)
                        .build();
                var observation = WeatherObservation.builder()
                        .city(city)
                        .weatherType(weatherType)
                        .temperature(temperature)
                        .dateTime(dateTime)
                        .id(id)
                        .build();
                weatherObservations.add(observation);
            }
            return weatherObservations;
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public WeatherObservation getMostRecentWeatherObservation(UUID cityId) {
        return getCityWeatherObservation(cityId).stream()
                .max(Comparator.comparing(WeatherObservation::getDateTime))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void updateWeatherObservation(UpdateWeatherObservationRequest request) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "UPDATE weather_observation SET temperature=?, weather_type_id=? WHERE id=?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setDouble(1, request.temperature());
            st.setInt(2, request.weatherTypeId());
            st.setString(3, request.weatherObservationId().toString());
            var updated = st.executeUpdate();
            if (updated == 0) {
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void deleteWeatherObservation(UUID id) {
        Connection con;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
        String query = "DELETE FROM weather_observation WHERE id=?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, id.toString());
            var updated = st.executeUpdate();
            if (updated == 0) {
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }
}
