package com.metromanage.model;

import com.metromanage.domain.Passenger;
import java.sql.*;

public class PassengerPersistanceHandler extends PersistanceHandler {

    public PassengerPersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        Passenger passenger = (Passenger) obj;
        if(passenger.getPassengerID() != 0){
            String updateQuery = "Update Passenger Set name = ?, email = ?, phoneNumber = ?, passwordHash = ?, status = ?, registrationDate = ?, walletBalance = ? Where passengerID = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(updateQuery)) {
                pstmt.setString(1, passenger.getName());
                pstmt.setString(2, passenger.getEmail());
                pstmt.setString(3, passenger.getPhoneNumber());
                pstmt.setString(4, passenger.getPasswordHash());
                pstmt.setString(5, passenger.getStatus());
                pstmt.setTimestamp(6, Timestamp.valueOf(passenger.getRegistrationDate()));
                pstmt.setFloat(7, passenger.getWalletBalance());
                pstmt.setInt(8, passenger.getPassengerID());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return passenger.getPassengerID();
        }
        String saveQuery = "Insert Into Passenger(name, email, phoneNumber, passwordHash, status, registrationDate, walletBalance) Values(?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, passenger.getName());
            pstmt.setString(2, passenger.getEmail());
            pstmt.setString(3, passenger.getPhoneNumber());
            pstmt.setString(4, passenger.getPasswordHash());
            pstmt.setString(5, passenger.getStatus());
            pstmt.setTimestamp(6, Timestamp.valueOf(passenger.getRegistrationDate()));
            pstmt.setFloat(7, passenger.getWalletBalance());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating passenger failed, no rows affected.");
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
        Passenger passenger = (Passenger) obj;
        passenger.setStatus("deleted");
        save(passenger);
    }

    @Override
    public Object find(int id) {
        String findQuery = "Select * From Passenger Where passengerID = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(findQuery)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Passenger passenger = new Passenger();
                    passenger.setPassengerID(id);
                    passenger.setName(rs.getString("name"));
                    passenger.setEmail(rs.getString("email"));
                    passenger.setPhoneNumber(rs.getString("phoneNumber"));
                    passenger.setPasswordHash(rs.getString("passwordHash"));
                    passenger.setStatus(rs.getString("status"));
                    passenger.setRegistrationDate(rs.getTimestamp("registrationDate").toLocalDateTime());
                    passenger.setWalletBalance(rs.getFloat("walletBalance"));
                    return passenger;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object findByEmail(String email) {
        String findQuery = "Select * From Passenger Where email = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(findQuery)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Passenger passenger = new Passenger();
                    passenger.setPassengerID(rs.getInt("passengerID"));
                    passenger.setName(rs.getString("name"));
                    passenger.setEmail(rs.getString("email"));
                    passenger.setPhoneNumber(rs.getString("phoneNumber"));
                    passenger.setPasswordHash(rs.getString("passwordHash"));
                    passenger.setStatus(rs.getString("status"));
                    passenger.setRegistrationDate(rs.getTimestamp("registrationDate").toLocalDateTime());
                    passenger.setWalletBalance(rs.getFloat("walletBalance"));
                    return passenger;
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
     * Search for passengers by name, email, or phone number
     * @param searchTerm The search string to match against
     * @param includeDeleted Whether to include passengers with "deleted" status
     * @return ArrayList of matching Passenger objects
     */
    public java.util.ArrayList<Passenger> searchPassengers(String searchTerm, boolean includeDeleted) 
    {
        java.util.ArrayList<Passenger> results = new java.util.ArrayList<>();

        String searchPattern = "%" + searchTerm + "%";
        
        String searchQuery;
        if (includeDeleted) 
        {
            searchQuery = "SELECT * FROM Passenger WHERE " +
                         "(name LIKE ? OR email LIKE ? OR phoneNumber LIKE ?) " +
                         "ORDER BY name";
        } 
        else 
        {
            searchQuery = "SELECT * FROM Passenger WHERE " +
                         "(name LIKE ? OR email LIKE ? OR phoneNumber LIKE ?) " +
                         "AND status != 'deleted' " +
                         "ORDER BY name";
        }
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(searchQuery)) 
        {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) 
            {
                while (rs.next()) 
                {
                    Passenger passenger = new Passenger();
                    passenger.setPassengerID(rs.getInt("passengerID"));
                    passenger.setName(rs.getString("name"));
                    passenger.setEmail(rs.getString("email"));
                    passenger.setPhoneNumber(rs.getString("phoneNumber"));
                    passenger.setPasswordHash(rs.getString("passwordHash"));
                    passenger.setStatus(rs.getString("status"));
                    passenger.setRegistrationDate(rs.getTimestamp("registrationDate").toLocalDateTime());
                    passenger.setWalletBalance(rs.getFloat("walletBalance"));
                    results.add(passenger);
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return results;
    }

    /**
     * Get all active passengers
     * @return ArrayList of all Passenger objects with active status
     */
    public java.util.ArrayList<Passenger> getAllPassengers()
    {
        java.util.ArrayList<Passenger> results = new java.util.ArrayList<>();
        String query = "SELECT * FROM Passenger WHERE status != 'deleted' ORDER BY name";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) 
        {
            try (ResultSet rs = pstmt.executeQuery()) 
            {
                while (rs.next()) 
                {
                    Passenger passenger = new Passenger();
                    passenger.setPassengerID(rs.getInt("passengerID"));
                    passenger.setName(rs.getString("name"));
                    passenger.setEmail(rs.getString("email"));
                    passenger.setPhoneNumber(rs.getString("phoneNumber"));
                    passenger.setPasswordHash(rs.getString("passwordHash"));
                    passenger.setStatus(rs.getString("status"));
                    passenger.setRegistrationDate(rs.getTimestamp("registrationDate").toLocalDateTime());
                    passenger.setWalletBalance(rs.getFloat("walletBalance"));
                    results.add(passenger);
                }
            }
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return results;
    }
}