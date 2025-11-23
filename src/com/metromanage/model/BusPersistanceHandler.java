package com.metromanage.model;

import com.metromanage.domain.Bus;
import java.sql.*;
import java.util.ArrayList;

public class BusPersistanceHandler extends PersistanceHandler {

    public BusPersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        Bus bus = (Bus) obj;
        if (bus.getBusID() != 0) {
            String updateQuery = "Update Bus Set plateNumber = ?, capacity = ?, status = ?, routeID = ? Where id = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(updateQuery)) {
                pstmt.setString(1, bus.getPlateNumber());
                pstmt.setInt(2, bus.getCapacity());
                pstmt.setString(3, bus.getStatus());
                pstmt.setInt(4, bus.getRouteID());
                pstmt.setInt(5, bus.getBusID());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return bus.getBusID();
        }
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
        Bus bus = (Bus) obj;
        bus.setStatus("Deleted");
        save(bus);
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

    public ArrayList<Bus> getBusesByRoute(int routeID) {
        String query = "Select * From Bus Where routeID = ?";
        ArrayList<Bus> buses = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setInt(1, routeID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bus bus = new Bus();
                    bus.setBusID(rs.getInt("id"));
                    bus.setPlateNumber(rs.getString("plateNumber"));
                    bus.setCapacity(rs.getInt("capacity"));
                    bus.setStatus(rs.getString("status"));
                    bus.setRouteID(rs.getInt("routeID"));
                    buses.add(bus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    public ArrayList<Bus> getFreeBuses() {
        String query = "Select * From Bus Where routeID IS NULL or routeID = 0";
        ArrayList<Bus> buses = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bus bus = new Bus();
                    bus.setBusID(rs.getInt("id"));
                    bus.setPlateNumber(rs.getString("plateNumber"));
                    bus.setCapacity(rs.getInt("capacity"));
                    bus.setStatus(rs.getString("status"));
                    bus.setRouteID(rs.getInt("routeID"));
                    buses.add(bus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    public ArrayList<Bus> getActiveBusesForRoute(int routeID) {
        String query = "Select * From Bus Where routeID = ? And status = 'Active'";
        ArrayList<Bus> buses = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setInt(1, routeID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bus bus = new Bus();
                    bus.setBusID(rs.getInt("id"));
                    bus.setPlateNumber(rs.getString("plateNumber"));
                    bus.setCapacity(rs.getInt("capacity"));
                    bus.setStatus(rs.getString("status"));
                    bus.setRouteID(rs.getInt("routeID"));
                    buses.add(bus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

        public ArrayList<Bus> getAllBuses() {
        String query = "Select * From Bus Where status != 'Deleted'";
        ArrayList<Bus> buses = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bus bus = new Bus();
                    bus.setBusID(rs.getInt("id"));
                    bus.setPlateNumber(rs.getString("plateNumber"));
                    bus.setCapacity(rs.getInt("capacity"));
                    bus.setStatus(rs.getString("status"));
                    bus.setRouteID(rs.getInt("routeID"));
                    buses.add(bus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    public ArrayList<Bus> searchBuses(String searchTerm, boolean includeDeleted) {
        ArrayList<Bus> results = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        String searchQuery;
        if (includeDeleted) {
            searchQuery = "SELECT * FROM Bus WHERE " +
                         "(plateNumber LIKE ? OR status LIKE ? OR CAST(capacity AS VARCHAR) LIKE ?) " +
                         "ORDER BY plateNumber";
        } else {
            searchQuery = "SELECT * FROM Bus WHERE " +
                         "(plateNumber LIKE ? OR status LIKE ? OR CAST(capacity AS VARCHAR) LIKE ?) " +
                         "AND status != 'Deleted' " +
                         "ORDER BY plateNumber";
        }
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(searchQuery)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bus bus = new Bus();
                    bus.setBusID(rs.getInt("id"));
                    bus.setPlateNumber(rs.getString("plateNumber"));
                    bus.setCapacity(rs.getInt("capacity"));
                    bus.setStatus(rs.getString("status"));
                    bus.setRouteID(rs.getInt("routeID"));
                    results.add(bus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return results;
    }
}