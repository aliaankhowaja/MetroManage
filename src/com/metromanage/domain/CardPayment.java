package com.metromanage.domain;

import com.metromanage.model.PaymentPersistanceHandler;

public class CardPayment implements Payment {
    private int paymentID;
    private int passengerID;
    private float amount;
    private String paymentDate;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;

    public CardPayment(int passengerID, float amount, String paymentDate, String cardNumber,
            String cardHolderName, String expiryDate) {
        this.passengerID = passengerID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        PaymentPersistanceHandler pph = new PaymentPersistanceHandler();
        this.paymentID = pph.savePayment(this, "Card");
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

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
    
}