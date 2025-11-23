package com.metromanage.model;

import com.metromanage.domain.Bus;
import com.metromanage.domain.Ride;
import com.metromanage.domain.Route;
import com.metromanage.domain.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RidePersistanceHandler extends PersistanceHandler {

    public RidePersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        
        Ride ride = (Ride) obj;
        int id = ride.getRideID();
        String saveQuery = "update ride set routeID = ?, busID = ?, boardingTime = ?, arrivalTime = ?, isActive = ?, boardingStationID = ?, arrivalStationID = ? where id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ride.getRoute().getRouteID());
            if(ride.getBus() != null) {
                pstmt.setInt(2, ride.getBus().getBusID());
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            if(ride.getBoardingTime() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(ride.getBoardingTime()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }
            if(ride.getArrivalTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(ride.getArrivalTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            pstmt.setBoolean(5, ride.isActive());
            if(ride.getBoardingStationID() != 0) {
                pstmt.setInt(6, ride.getBoardingStationID());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            if(ride.getArrivalStationID() != 0) {
                pstmt.setInt(7, ride.getArrivalStationID());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            pstmt.setLong(8, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
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
        String findQuery = "Select * From ride Where id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(findQuery)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Ride ride = new Ride();
                    ride.setRideID(id);
                    Route route = (Route) new RoutePersistanceHandler().find(rs.getInt("routeID"));
                    ride.setRoute(route);
                    Bus bus = null;
                    int busId = rs.getInt("busID");
                    if (!rs.wasNull()) {
                        bus = (Bus) new BusPersistanceHandler().find((int) busId);
                    }
                    ride.setBus(bus);
                    ride.setBoardingTime(
                            rs.getTimestamp("boardingTime") != null ? rs.getTimestamp("boardingTime").toLocalDateTime()
                                    : null);
                    ride.setArrivalTime(
                            rs.getTimestamp("arrivalTime") != null ? rs.getTimestamp("arrivalTime").toLocalDateTime()
                                    : null);
                    ride.setActive(rs.getBoolean("isActive"));
                    int boardingStationID = rs.getInt("boardingStationID");
                    if (!rs.wasNull()) {
                        ride.setBoardingStationID(boardingStationID);
                    }
                    int arrivalStationID = rs.getInt("arrivalStationID");
                    if (!rs.wasNull()) {
                        ride.setArrivalStationID(arrivalStationID);
                    }
                    Ticket ticket = (Ticket) new TicketPersistanceHandler().find(rs.getInt("id"));
                    ride.setTicket(ticket);
                    return ride;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int getTodayTripsCount() {
        String query = "SELECT COUNT(*) as tripCount FROM ride WHERE CAST(boardingTime AS DATE) = CAST(GETDATE() AS DATE) AND isActive = 1";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tripCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get boarding data grouped by bus and route
     * @param routeID Filter by route (0 for all routes)
     * @param daysBack Number of days to look back (0 for today, 7 for last 7 days, etc.)
     * @param timeWindow Filter by time: "All" (0-23), "Morning" (6-11), "Evening" (17-22)
     * @return ArrayList of BoardingData objects
     */
    public ArrayList<BoardingData> getBoardingData(int routeID, int daysBack, String timeWindow) {
        ArrayList<BoardingData> boardingDataList = new ArrayList<>();
        
        // Build query with filters - simpler approach to show individual rides
        StringBuilder query = new StringBuilder();
        query.append("SELECT r.id, r.busID, r.routeID, ro.routeName, ");
        query.append("r.boardingTime, r.isActive ");
        query.append("FROM ride r ");
        query.append("LEFT JOIN route ro ON r.routeID = ro.id ");
        query.append("WHERE 1=1 ");
        
        // Date range filter
        if (daysBack == 0) {
            // Today only
            query.append("AND CAST(r.boardingTime AS DATE) = CAST(GETDATE() AS DATE) ");
        } else if (daysBack > 0) {
            // Last N days
            query.append("AND r.boardingTime >= DATEADD(day, -").append(daysBack).append(", GETDATE()) ");
        }
        // If daysBack is -1 or negative, show all data (no date filter)
        
        // Route filter
        if (routeID > 0) {
            query.append("AND r.routeID = ? ");
        }
        
        // Time window filter
        if ("Morning".equals(timeWindow)) {
            query.append("AND DATEPART(HOUR, r.boardingTime) >= 6 AND DATEPART(HOUR, r.boardingTime) < 12 ");
        } else if ("Evening".equals(timeWindow)) {
            query.append("AND DATEPART(HOUR, r.boardingTime) >= 17 AND DATEPART(HOUR, r.boardingTime) < 23 ");
        }
        
        query.append("ORDER BY r.boardingTime DESC");
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query.toString())) {
            // Set route parameter if needed
            if (routeID > 0) {
                pstmt.setInt(1, routeID);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Group rides by bus, route, and rounded boarding time (to aggregate passengers)
                java.util.Map<String, BoardingData> tripMap = new java.util.LinkedHashMap<>();
                
                while (rs.next()) {
                    int busID = rs.getInt("busID");
                    int rID = rs.getInt("routeID");
                    String routeName = rs.getString("routeName");
                    Timestamp boardingTime = rs.getTimestamp("boardingTime");
                    
                    if (boardingTime != null) {
                        // Create a key to group trips (same bus, route, and hour)
                        String timeKey = boardingTime.toString().substring(0, 13); // Group by hour
                        String tripKey = busID + "_" + rID + "_" + timeKey;
                        
                        if (tripMap.containsKey(tripKey)) {
                            // Increment passenger count for existing trip
                            tripMap.get(tripKey).passengerCount++;
                        } else {
                            // Create new trip entry
                            BoardingData data = new BoardingData();
                            data.busID = busID;
                            data.routeID = rID;
                            data.routeName = routeName != null ? routeName : "Unknown";
                            data.boardingTime = boardingTime.toString();
                            data.passengerCount = 1;
                            tripMap.put(tripKey, data);
                        }
                    }
                }
                
                boardingDataList.addAll(tripMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return boardingDataList;
    }
    
    /**
     * Get total boarding count across all filtered rides
     */
    public int getTotalBoardings(int routeID, int daysBack, String timeWindow) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) as total FROM ride WHERE 1=1 ");
        
        // Date range filter
        if (daysBack == 0) {
            query.append("AND CAST(boardingTime AS DATE) = CAST(GETDATE() AS DATE) ");
        } else if (daysBack > 0) {
            query.append("AND boardingTime >= DATEADD(day, -").append(daysBack).append(", GETDATE()) ");
        }
        // If daysBack is -1, show all data (no date filter)
        
        // Route filter
        if (routeID > 0) {
            query.append("AND routeID = ? ");
        }
        
        // Time window filter
        if ("Morning".equals(timeWindow)) {
            query.append("AND DATEPART(HOUR, boardingTime) >= 6 AND DATEPART(HOUR, boardingTime) < 12 ");
        } else if ("Evening".equals(timeWindow)) {
            query.append("AND DATEPART(HOUR, boardingTime) >= 17 AND DATEPART(HOUR, boardingTime) < 23 ");
        }
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query.toString())) {
            if (routeID > 0) {
                pstmt.setInt(1, routeID);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get busiest route for filtered rides
     * @return Map with route name and boarding count
     */
    public Map<String, Integer> getBusiestRoute(int daysBack, String timeWindow) {
        Map<String, Integer> result = new HashMap<>();
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT TOP 1 ro.routeName, COUNT(*) as boardingCount ");
        query.append("FROM ride r ");
        query.append("LEFT JOIN route ro ON r.routeID = ro.id ");
        query.append("WHERE 1=1 ");
        
        // Date range filter
        if (daysBack == 0) {
            query.append("AND CAST(r.boardingTime AS DATE) = CAST(GETDATE() AS DATE) ");
        } else if (daysBack > 0) {
            query.append("AND r.boardingTime >= DATEADD(day, -").append(daysBack).append(", GETDATE()) ");
        }
        // If daysBack is -1, show all data (no date filter)
        
        // Time window filter
        if ("Morning".equals(timeWindow)) {
            query.append("AND DATEPART(HOUR, r.boardingTime) >= 6 AND DATEPART(HOUR, r.boardingTime) < 12 ");
        } else if ("Evening".equals(timeWindow)) {
            query.append("AND DATEPART(HOUR, r.boardingTime) >= 17 AND DATEPART(HOUR, r.boardingTime) < 23 ");
        }
        
        query.append("GROUP BY ro.routeName ");
        query.append("ORDER BY boardingCount DESC");
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query.toString())) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.put(rs.getString("routeName"), rs.getInt("boardingCount"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Inner class to hold boarding data
     */
    public static class BoardingData {
        public int busID;
        public int routeID;
        public String routeName;
        public String boardingTime;
        public int passengerCount;
    }
    
}