package com.metromanage.model;

import com.metromanage.domain.Route;
import java.sql.*;
import java.util.ArrayList;

public class RoutePersistanceHandler extends PersistanceHandler {

    public RoutePersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        Route route = (Route) obj;
        String saveQuery = "Insert Into Route(routeName, totalDistance, estimatedTime, isActive, cost) Values(?,?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, route.getRouteName());
            pstmt.setDouble(2, route.getTotalDistance());
            pstmt.setInt(3, route.getEstimatedTime());
            pstmt.setBoolean(4, route.getActive());
            pstmt.setDouble(5, route.getCost());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating route failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating route failed, no ID obtained.");
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
        String findQuery = "Select * From route Where id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(findQuery)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Route route = new Route();
                    route.setRouteID(id);
                    route.setRouteName(rs.getString("routeName"));
                    route.setTotalDistance(rs.getFloat("totalDistance"));
                    route.setEstimatedTime(rs.getInt("estimatedTime"));
                    route.setActive(rs.getBoolean("isActive"));
                    route.setCost(rs.getFloat("cost"));
                    return route;
                    
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Route> getAllRoutes() {
        String query = "Select * From route Where isActive = 1";
        ArrayList<Route> routes = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Route route = new Route();
                    route.setRouteID(rs.getInt("id"));
                    route.setRouteName(rs.getString("routeName"));
                    route.setTotalDistance(rs.getFloat("totalDistance"));
                    route.setEstimatedTime(rs.getInt("estimatedTime"));
                    route.setActive(rs.getBoolean("isActive"));
                    route.setCost(rs.getFloat("cost"));
                    routes.add(route);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }
    
    public int getTotalActiveBuses() {
        String query = "SELECT COUNT(*) as busCount FROM Bus WHERE status = 'Active'";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("busCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}