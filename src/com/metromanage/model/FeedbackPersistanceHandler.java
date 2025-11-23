package com.metromanage.model;

import com.metromanage.domain.Feedback;
import java.sql.*;
import java.util.ArrayList;

public class FeedbackPersistanceHandler extends PersistanceHandler {

    public FeedbackPersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        Feedback feedback = (Feedback) obj;
        String saveQuery = "Insert Into Feedback(passengerID, type, comments, timestamp) Values(?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, feedback.getPassengerID());
            pstmt.setString(2, feedback.getType());
            pstmt.setString(3, feedback.getComments());
            pstmt.setTimestamp(4, Timestamp.valueOf(feedback.getTimestamp()));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bus failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating Feedback failed, no ID obtained.");
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
        String findQuery = "Select * From Feedback Where id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(findQuery)) {
            pstmt.setLong(1, numericId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setFeedbackID(id);
                    feedback.setPassengerID(rs.getInt("passengerID"));
                    feedback.setType(rs.getString("type"));
                    feedback.setComments(rs.getString("comments"));
                    feedback.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return feedback;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all feedback from the database
     */
    public ArrayList<Feedback> getAllFeedback() {
        ArrayList<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT * FROM Feedback ORDER BY timestamp DESC";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setFeedbackID(rs.getInt("id"));
                    feedback.setPassengerID(rs.getInt("passengerID"));
                    feedback.setType(rs.getString("type"));
                    feedback.setComments(rs.getString("comments"));
                    Timestamp ts = rs.getTimestamp("timestamp");
                    if (ts != null) {
                        feedback.setTimestamp(ts.toLocalDateTime());
                    }
                    feedbackList.add(feedback);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return feedbackList;
    }
    
    /**
     * Get feedback by passenger ID
     */
    public ArrayList<Feedback> getFeedbackByPassenger(int passengerID) {
        ArrayList<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT * FROM Feedback WHERE passengerID = ? ORDER BY timestamp DESC";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setInt(1, passengerID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setFeedbackID(rs.getInt("id"));
                    feedback.setPassengerID(rs.getInt("passengerID"));
                    feedback.setType(rs.getString("type"));
                    feedback.setComments(rs.getString("comments"));
                    Timestamp ts = rs.getTimestamp("timestamp");
                    if (ts != null) {
                        feedback.setTimestamp(ts.toLocalDateTime());
                    }
                    feedbackList.add(feedback);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return feedbackList;
    }
    
    /**
     * Get feedback count
     */
    public int getTotalFeedbackCount() {
        String query = "SELECT COUNT(*) as count FROM Feedback";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}