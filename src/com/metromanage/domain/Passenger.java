package com.metromanage.domain;

public class Passenger{
    private String passengerID;
    private String name;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private String status;
    private String registrationDate;

    public Passenger(String passengerID, String name, String email, String phoneNumber) {
        this.passengerID = passengerID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void applyChanges(){

    }

    public void markDeleted(){

    }
    
}