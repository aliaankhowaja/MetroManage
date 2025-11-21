package com.metromanage.domain;

import com.metromanage.model.PaymentPersistanceHandler;
public class CashPayment implements Payment {
    private int paymentID;
    private int passengerID;
    private float amount;
    private String paymentDate;
    
    public CashPayment(int passengerID, float amount, String paymentDate) {
        this.passengerID = passengerID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        PaymentPersistanceHandler pph = new PaymentPersistanceHandler();
        this.paymentID = pph.savePayment(this, "Cash");
    }
   
    public CashPayment(float amount, String paymentDate) {
        this.passengerID = 0;
        this.amount = amount;
        this.paymentDate = paymentDate;
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