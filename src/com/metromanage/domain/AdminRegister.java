package com.metromanage.domain;

import java.sql.Connection;

import com.metromanage.model.PassengerPersistanceHandler;

public class AdminRegister {
    Connection dbConnection;

    public AdminRegister(Connection connection) {
        this.dbConnection = connection;
    }

    public Passenger addPassenger(String name, String email, String phoneNumber, String password, Float walletBalance) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler(dbConnection);
        if(pph.findByEmail(email) != null){
            System.out.println("Passenger with this email already exists.");
            return null;
        }
        return new Passenger(name, email, phoneNumber, Passenger.GenerateHash(password), walletBalance, dbConnection);
    }

    public void updatePassenger(String email, String name, String phoneNumber, String password, String status,
            Float walletBalance) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler(dbConnection);
        Passenger passenger = (Passenger) pph.findByEmail(email);
        passenger.setName(name);
        passenger.setPhoneNumber(phoneNumber);
        passenger.setPasswordHash(Passenger.GenerateHash(password));
        passenger.setStatus(status);
        passenger.setWalletBalance(walletBalance);
        pph.save(passenger);

    }
    
    public void deletePassenger(String email) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler(dbConnection);
        Passenger passenger = (Passenger) pph.findByEmail(email);
        if (passenger != null) {
            passenger.markDeleted(dbConnection);
        } else {
            System.out.println("Passenger not found.");
        }
    }

}
