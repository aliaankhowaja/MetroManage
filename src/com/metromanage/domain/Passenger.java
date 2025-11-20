package com.metromanage.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.time.LocalDateTime;

import com.metromanage.model.PassengerPersistanceHandler;

public class Passenger{
    private int passengerID;
    private String name;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private String status;
    private LocalDateTime registrationDate;
    private Float walletBalance;

    public Passenger() { }

    public Passenger(int passengerID, String name, String email, String phoneNumber, String passwordHash, String status, LocalDateTime registrationDate, Float walletBalance) {
        this.passengerID = passengerID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.status = status;
        this.registrationDate = registrationDate;
        this.walletBalance = walletBalance;
    }

    public Passenger(String name, String email, String phoneNumber, String passwordHash, Float walletBalance, Connection connection) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.status = "Active";
        this.registrationDate = LocalDateTime.now();
        this.walletBalance = walletBalance;
        PassengerPersistanceHandler PassengerPersistanceHandler = new PassengerPersistanceHandler(connection);
        this.passengerID = PassengerPersistanceHandler.save(this);
    }
    
    public void applyChanges(){

    }

    public void markDeleted(Connection connection) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler(connection);
        pph.delete(this);
    }
    
    public Float getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Float walletBalance) {
        this.walletBalance = walletBalance;
    }

    public int getPassengerID() {
        return passengerID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setPassengerID(int passengerID) {
        this.passengerID = passengerID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public static String GenerateHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}