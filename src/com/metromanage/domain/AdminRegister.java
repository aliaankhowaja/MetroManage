package com.metromanage.domain;

import java.sql.Connection;

import com.metromanage.model.BusPersistanceHandler;
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

    public Bus addBus(String licensePlate, int capacity, String status, int routeID) {
        return new Bus(licensePlate, capacity, status, routeID);
    }

    public void updateBus(int busID, String licensePlate, int capacity, String status, int routeID) {
        BusPersistanceHandler bph = new BusPersistanceHandler();
        Bus bus = (Bus) bph.find(busID);
        bus.setPlateNumber(licensePlate);
        bus.setCapacity(capacity);
        bus.setStatus(status);
        bus.setRouteID(routeID);
        bph.save(bus);
    }

    public void deleteBus(int busID) {
        BusPersistanceHandler bph = new BusPersistanceHandler();
        Bus bus = (Bus) bph.find(busID);
        if (bus != null) {
            bus.markDeleted();
        } else {
            System.out.println("Bus not found.");
        }
    }

}
