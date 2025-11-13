package com.metromanage.domain;

public class CashPayment implements Payment {
    private String paymentID;
    private String passengerID;
    private String amount;
    private String paymentDate;
    

    public CashPayment(String paymentID, String passengerID, String amount, String paymentDate) {
        this.paymentID = paymentID;
        this.passengerID = passengerID;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
   
}