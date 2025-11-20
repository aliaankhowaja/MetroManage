package com.metromanage.model;

import java.sql.Connection;

import com.metromanage.domain.Ticket;
import java.sql.*;

public class TicketPersistanceHandler extends PersistanceHandler {

    public TicketPersistanceHandler(Connection connection) {
        this.dbConnection = connection;
    }

    @Override
    public int save(Object obj) {
        Ticket ticket = (Ticket) obj;
        if(ticket.getTicketID() != 0){
            String updateQuery = "Update ride Set ticketIssueTime = ?, ticketExpiryTime = ?, ticketStatus = ?, paymentID = ? Where id = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(updateQuery)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(ticket.getIssueTime()));
                pstmt.setTimestamp(2, Timestamp.valueOf(ticket.getExpiryTime()));
                pstmt.setString(3, ticket.getStatus());
                pstmt.setInt(4, ticket.getPaymentID());
                pstmt.setInt(5, ticket.getTicketID());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ticket.getTicketID();
        }
        String saveQuery = "Insert Into ride(ticketIssueTime, ticketExpiryTime, ticketStatus, paymentID) Values(?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(ticket.getIssueTime()));
            pstmt.setTimestamp(2, Timestamp.valueOf(ticket.getExpiryTime()));
            pstmt.setString(3, ticket.getStatus());
            pstmt.setInt(4, ticket.getPaymentID());
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
                    Ticket ticket = new Ticket();
                    ticket.setTicketID(id);
                    ticket.setIssueTime(rs.getTimestamp("ticketIssueTime").toLocalDateTime());
                    ticket.setExpiryTime(rs.getTimestamp("ticketExpiryTime").toLocalDateTime());
                    
                    ticket.setStatus(rs.getString("ticketStatus"));
                    ticket.setPaymentID(rs.getInt("paymentID"));
                    return ticket;
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