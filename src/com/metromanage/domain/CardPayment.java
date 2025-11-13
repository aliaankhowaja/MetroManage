package com.metromanage.domain;

public class CardPayment implements Payment {
    private String paymentID;
    private String passengerID;
    private String amount;
    private String paymentDate;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;

    public CardPayment(String paymentID, String passengerID, String amount, String paymentDate, String cardNumber, String cardHolderName, String expiryDate) {
        this.paymentID = paymentID;
        this.passengerID = passengerID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
    }
}