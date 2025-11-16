package com.metromanage.domain;

public class Passenger{
    private int passengerID;
    private String name;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private String status;
    private String registrationDate;
    private Float walletBalance;

    public Passenger(int passengerID, String name, String email, String phoneNumber) {
        this.passengerID = passengerID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void applyChanges(){

    }

    public void markDeleted() {

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
}