package com.metromanage.model;

import com.metromanage.domain.Bus;
import com.metromanage.domain.Ride;
import com.metromanage.domain.Route;
import com.metromanage.domain.Ticket;
import java.sql.*;

public class RidePersistanceHandler extends PersistanceHandler {

    public RidePersistanceHandler(Connection connection) {
        this.dbConnection = connection;
    }

    @Override
    public int save(Object obj) {
        
        Ride ride = (Ride) obj;
        int id = ride.getRideID();
        String saveQuery = "update ride set routeID = ?, busID = ?, boardingTime = ?, arrivalTime = ?, isActive = ? where id = ?";
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
            pstmt.setLong(6, id);
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
                    Route route = (Route) new RoutePersistanceHandler(dbConnection).find(rs.getInt("routeID"));
                    ride.setRoute(route);
                    Bus bus = null;
                    int busId = rs.getInt("busID");
                    if(!rs.wasNull()) {
                        bus = (Bus) new BusPersistanceHandler(dbConnection).find((int) busId);
                    }
                    ride.setBus(bus);
                    ride.setBoardingTime(rs.getTimestamp("boardingTime") != null ? rs.getTimestamp("boardingTime").toLocalDateTime() : null);
                    ride.setArrivalTime(rs.getTimestamp("arrivalTime") != null ? rs.getTimestamp("arrivalTime").toLocalDateTime() : null);
                    ride.setActive(rs.getBoolean("isActive"));
                    Ticket ticket = (Ticket) new TicketPersistanceHandler(dbConnection).find(rs.getInt("ticketID"));
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
}