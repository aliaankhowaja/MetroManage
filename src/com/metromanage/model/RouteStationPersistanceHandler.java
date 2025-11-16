package com.metromanage.model;

import java.sql.*;
import java.util.ArrayList;

public class RouteStationPersistanceHandler extends PersistanceHandler {

    public RouteStationPersistanceHandler(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public int save(Object obj) {

        return 0;
    }

    @Override
    public void delete(Object obj) {
        
    }

    @Override
    public Object find(int id) {
        return null;
    }

    public void assignStationToRoute(int stationID, int routeID) {
        String insertQuery = "Insert Into RouteStation(stationID, routeID) Values(?,?)";
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setInt(1, stationID);
            pstmt.setInt(2, routeID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void removeStationFromRoute(int stationID, int routeID) {
        String deleteQuery = "Delete From RouteStation Where stationID = ? And routeID = ?";
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, stationID);
            pstmt.setInt(2, routeID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getStationsByRoute(int routeID) {
        String query = "Select stationID From RouteStation Where routeID = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setInt(1, routeID);
            try (ResultSet rs = pstmt.executeQuery()) {
                ArrayList<String> stationIDs = new ArrayList<>();
                while (rs.next()) {
                    stationIDs.add(rs.getString("stationID"));
                }
                return stationIDs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getRoutesByStation(int stationID) {
        String query = "Select routeID From RouteStation Where stationID = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setInt(1, stationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                ArrayList<String> routeIDs = new ArrayList<>();
                while (rs.next()) {
                    routeIDs.add(rs.getString("routeID"));
                }
                return routeIDs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    
}
