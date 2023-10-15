package com.mashanlote.services;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Qualifier("JDBC")
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
        String query = "DELETE FROM weather_type WHERE id=?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);
            int deleted = st.executeUpdate();
            if (deleted == 0) throw new NotFoundException();
        } catch (SQLException e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void createWeatherObservation(CreateWeatherObservationRequest request) {

    }

    @Override
    public List<WeatherObservation> getCityWeatherObservation(UUID cityId) {
        return null;
    }

    @Override
    public WeatherObservation getMostRecentWeatherObservation(UUID cityId) {
        return null;
    }

    @Override
    public void updateWeatherObservation(UpdateWeatherObservationRequest request) {

    }

    @Override
    public void deleteWeatherObservation(UUID id) {

    }
}
