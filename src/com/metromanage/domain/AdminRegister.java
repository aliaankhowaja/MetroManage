package com.metromanage.domain;

import java.sql.Connection;

import com.metromanage.model.DB;
import com.metromanage.model.PassengerPersistanceHandler;

public class AdminRegister {
    Connection dbConnection;

    public AdminRegister() {
        this.dbConnection = DB.getConnection();
    }

    public Passenger addPassenger(String name, String email, String phoneNumber, String password, Float walletBalance) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
        if(pph.findByEmail(email) != null){
            System.out.println("Passenger with this email already exists.");
            return null;
        }
        return new Passenger(name, email, phoneNumber, Passenger.GenerateHash(password), walletBalance);
    }

    public void updatePassenger(String email, String name, String phoneNumber, String password, String status,
            Float walletBalance) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
        Passenger passenger = (Passenger) pph.findByEmail(email);
        passenger.setName(name);
        passenger.setPhoneNumber(phoneNumber);
        passenger.setPasswordHash(Passenger.GenerateHash(password));
        passenger.setStatus(status);
        passenger.setWalletBalance(walletBalance);
        pph.save(passenger);

    }
    
    public void deletePassenger(String email) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
        Passenger passenger = (Passenger) pph.findByEmail(email);
        if (passenger != null) {
            passenger.markDeleted();
        } else {
            System.out.println("Passenger not found.");
        }
    }

}
