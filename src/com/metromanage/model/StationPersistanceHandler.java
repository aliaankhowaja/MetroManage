package com.metromanage.model;

import com.metromanage.domain.BoardingTotal;
import com.metromanage.domain.Station;
import java.sql.*;
import java.util.ArrayList;

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

    // public BoardingTotal getBoardingTotalsByDay(int stationID) {
    //     String query = "SELECT s.name AS stationName, r.name AS routeName, COUNT(*) AS totalBoardings, DAY(rid.boardingTime) AS day, MONTH(rid.boardingTime) AS month "
    //             +
    //             "FROM Ride rid " +
    //             "JOIN Route r ON rid.routeID = r.routeID " +
    //             "JOIN Station s ON rid.boardingStationID = s.stationID " +
    //             "WHERE rid.boardingStationID = ? " +
    //             "GROUP BY day, month, s.name, r.name";
    //     try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
    //         pstmt.setInt(1, stationID);
    //         try (ResultSet rs = pstmt.executeQuery()) {
    //             if (rs.next()) {
    //                 BoardingTotal bt = new BoardingTotal(
    //                         rs.getString("stationName"),
    //                         rs.getString("routeName"),
    //                         rs.getInt("totalBoardings"),
    //                         rs.getInt("day"),
    //                         rs.getInt("month"),
    //                         rs.getInt("year"));
    //                 return bt;
    //             } else {
    //                 return null;
    //             }
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
    
    // public BoardingTotal getBoardingTotalsByMonth(int stationID) {
    //     String query = "SELECT s.name AS stationName, r.name AS routeName, COUNT(*) AS totalBoardings, MONTH(rid.boardingTime) AS month, YEAR(rid.boardingTime) AS year "
    //             +
    //             "FROM Ride rid " +
    //             "JOIN Route r ON rid.routeID = r.routeID " +
    //             "JOIN Station s ON rid.boardingStationID = s.stationID " +
    //             "WHERE rid.boardingStationID = ? " +
    //             "GROUP BY month, year, s.name, r.name";
    //     try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
    //         pstmt.setInt(1, stationID);
    //         try (ResultSet rs = pstmt.executeQuery()) {
    //             if (rs.next()) {
    //                 BoardingTotal bt = new BoardingTotal(
    //                         rs.getString("stationName"),
    //                         rs.getString("routeName"),
    //                         rs.getInt("totalBoardings"),
    //                         0,
    //                         rs.getInt("month"),
    //                         rs.getInt("year"));
    //                 return bt;
    //             } else {
    //                 return null;
    //             }
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }

    // public BoardingTotal getBoardingTotalsByYear(int stationID) {
    //     String query = "SELECT s.name AS stationName, r.name AS routeName, COUNT(*) AS totalBoardings, YEAR(rid.boardingTime) AS year "
    //             +
    //             "FROM Ride rid " +
    //             "JOIN Route r ON rid.routeID = r.routeID " +
    //             "JOIN Station s ON rid.boardingStationID = s.stationID " +
    //             "WHERE rid.boardingStationID = ? " +
    //             "GROUP BY year, s.name, r.name";
    //     try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
    //         pstmt.setInt(1, stationID);
    //         try (ResultSet rs = pstmt.executeQuery()) {
    //             if (rs.next()) {
    //                 BoardingTotal bt = new BoardingTotal(
    //                         rs.getString("stationName"),
    //                         rs.getString("routeName"),
    //                         rs.getInt("totalBoardings"),
    //                         0,
    //                         0,
    //                         rs.getInt("year"));
    //                 return bt;
    //             } else {
    //                 return null;
    //             }
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }

    public ArrayList<BoardingTotal> getBoardingTotalsByDay() {
        String query = "SELECT s.name AS stationName, r.name AS routeName, COUNT(*) AS totalBoardings, DAY(rid.boardingTime) AS day, MONTH(rid.boardingTime) AS month " +
                "FROM Ride rid " +
                "JOIN Route r ON rid.routeID = r.routeID " +
                "JOIN Station s ON rid.boardingStationID = s.stationID " +
                "GROUP BY day, month, s.name, r.name";
        ArrayList<BoardingTotal> boardingTotals = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardingTotal bt = new BoardingTotal(
                            rs.getString("stationName"),
                            rs.getString("routeName"),
                            rs.getInt("totalBoardings"),
                            rs.getInt("day"),
                            rs.getInt("month"),
                            0);
                    boardingTotals.add(bt);
                }
                return boardingTotals;
            } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }} catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<BoardingTotal> getBoardingTotalsByMonth() {
        String query = "SELECT s.name AS stationName, r.name AS routeName, COUNT(*) AS totalBoardings, MONTH(rid.boardingTime) AS month, YEAR(rid.boardingTime) AS year "
                +
                "FROM Ride rid " +
                "JOIN Route r ON rid.routeID = r.routeID " +
                "JOIN Station s ON rid.boardingStationID = s.stationID " +
                "GROUP BY month, year, s.name, r.name";
        ArrayList<BoardingTotal> boardingTotals = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    BoardingTotal bt = new BoardingTotal(
                            rs.getString("stationName"),
                            rs.getString("routeName"),
                            rs.getInt("totalBoardings"),
                            0,
                            rs.getInt("month"),
                            rs.getInt("year"));
                    boardingTotals.add(bt);
                }
                return boardingTotals;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<BoardingTotal> getBoardingTotalsByYear() {
        String query = "SELECT s.name AS stationName, r.name AS routeName, COUNT(*) AS totalBoardings, YEAR(rid.boardingTime) AS year "
                +
                "FROM Ride rid " +
                "JOIN Route r ON rid.routeID = r.routeID " +
                "JOIN Station s ON rid.boardingStationID = s.stationID " +
                "GROUP BY year, s.name, r.name";
        ArrayList<BoardingTotal> boardingTotals = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardingTotal bt = new BoardingTotal(
                            rs.getString("stationName"),
                            rs.getString("routeName"),
                            rs.getInt("totalBoardings"),
                            0,
                            0,
                            rs.getInt("year"));
                    boardingTotals.add(bt);
                }
                return boardingTotals;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<BoardingTotal> getBoardingTotalsByHour(int stationID) {
        String query = "SELECT s.name AS stationName, r.name AS routeName, COUNT(*) AS totalBoardings, HOUR(rid.boardingTime) AS hour "+
                "FROM Ride rid " +
                "JOIN Route r ON rid.routeID = r.routeID " +
                "JOIN Station s ON rid.boardingStationID = s.stationID " +
                "WHERE rid.boardingStationID = ? " +
                "GROUP BY hour, s.name, r.name"+
                " ORDER BY hour ASC";
        ArrayList<BoardingTotal> boardingTotals = new ArrayList<>();
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setInt(1, stationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardingTotal bt = new BoardingTotal(
                            rs.getString("stationName"),
                            rs.getString("routeName"),
                            rs.getInt("totalBoardings"),
                            rs.getInt("hour"),
                            0,
                            0);
                    boardingTotals.add(bt);
                }
                return boardingTotals;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
