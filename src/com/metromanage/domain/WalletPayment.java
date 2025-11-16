package com.metromanage.domain;

import java.sql.Connection;

import com.metromanage.model.PaymentPersistanceHandler;

public class WalletPayment implements Payment {
    private int paymentID;
    private int passengerID;
    private float amount;
    private String paymentDate;

    public WalletPayment(int passengerID, float amount, String paymentDate, Connection connection) {

        this.passengerID = passengerID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        PaymentPersistanceHandler pph = new PaymentPersistanceHandler(connection);
        this.paymentID = pph.savePayment(this, "Wallet");
    }

    public int getPaymentID() {
        return paymentID;
    }

    public int getPassengerID() {
        return passengerID;
    }

    public float getAmount() {
        return amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }
    
    
    
}