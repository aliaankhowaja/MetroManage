package com.metromanage.model;

import com.metromanage.domain.Station;
import java.sql.*;

public class StationPersistanceHandler extends PersistanceHandler {

    public StationPersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        Station station = (Station) obj;
        String saveQuery = "Insert Into Station(name, latitude, longitude, status) Values(?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, station.getName());
            pstmt.setFloat(2, station.getLatitude());
            pstmt.setFloat(3, station.getLongitude());
            pstmt.setString(4, station.getStatus());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bus failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating Station failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void delete(Object obj) {
        // Implementation to delete Ticket object from DB
    }

    @Override
    public Object find(int id) {
        long numericId = id;
        String findQuery = "Select * From Station Where id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(findQuery)) {
            pstmt.setLong(1, numericId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Station station = new Station();
                    station.setStationID(id);
                    station.setName(rs.getString("name"));
                    station.setLatitude(rs.getFloat("latitude"));
                    station.setLongitude(rs.getFloat("longitude"));
                    station.setStatus(rs.getString("status"));
                    return station;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}