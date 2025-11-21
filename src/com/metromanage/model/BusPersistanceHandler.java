package com.metromanage.model;

import com.metromanage.domain.Bus;
import java.sql.*;

public class BusPersistanceHandler extends PersistanceHandler {

    public BusPersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        Bus bus = (Bus) obj;
        String saveQuery = "Insert Into Bus(plateNumber, capacity, status, routeID) Values(?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, bus.getPlateNumber());
            pstmt.setInt(2, bus.getCapacity());
            pstmt.setString(3, bus.getStatus());
            pstmt.setInt(4, bus.getRouteID());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bus failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating bus failed, no ID obtained.");
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
        String findQuery = "Select * From Bus Where id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(findQuery)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Bus bus = new Bus();
                    bus.setBusID(id);
                    bus.setPlateNumber(rs.getString("plateNumber"));
                    bus.setCapacity(rs.getInt("capacity"));
                    bus.setStatus(rs.getString("status"));
                    bus.setRouteID(rs.getInt("routeID"));
                    return bus;
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