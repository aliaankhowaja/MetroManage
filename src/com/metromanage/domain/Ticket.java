package com.metromanage.domain;

import java.time.LocalDateTime;

import com.metromanage.model.PassengerPersistanceHandler;
import com.metromanage.model.TicketPersistanceHandler;

public class Ticket{
    private int ticketID;
    private Ride ride;
    private LocalDateTime issueTime;
    private LocalDateTime expiryTime;
    private String status;
    private int paymentID;

    public Ticket(Passenger passenger, float cost, String paymentMethod, String paymentDetails) {
        this.issueTime = LocalDateTime.now();
        this.expiryTime = issueTime.plusHours(3); // Ticket valid for 3 hours
        this.status = "Active";
        Payment payment;
        // Process payment and generate paymentID
        if (paymentMethod.equals("Wallet")) {
            // Deduct from passenger's wallet
            Float currentBalance = passenger.getWalletBalance();
            if (currentBalance != null && currentBalance >= cost) {
                passenger.setWalletBalance(currentBalance - cost);
                payment = new WalletPayment(passenger.getPassengerID(), cost,
                        LocalDateTime.now().toString());
                paymentID = payment.getPaymentID();
            } else {
                throw new IllegalArgumentException("Insufficient wallet balance.");
            }
        } else if (paymentMethod.equals("Card")) {
            String details[] = paymentDetails.split(",");
            payment = new CardPayment(passenger.getPassengerID(), cost, LocalDateTime.now().toString(),
                    details[0], details[1], details[2]);
            this.paymentID = payment.getPaymentID();
        } else if (paymentMethod.equals("Cash")) {
            payment = new CashPayment(passenger.getPassengerID(), cost, LocalDateTime.now().toString());
            this.paymentID = payment.getPaymentID();
        } else {
            throw new IllegalArgumentException("Unsupported payment method.");
        }
        TicketPersistanceHandler tph = new TicketPersistanceHandler();
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
        pph.save(passenger); // Update passenger wallet balance
        this.ticketID = tph.save(this);

    }
    

    public Ticket(float cost, String paymentMethod, String paymentDetails) {
        this.issueTime = LocalDateTime.now();
        this.expiryTime = issueTime.plusHours(3); // Ticket valid for 3 hours
        this.status = "Active";
        Payment payment;
        // Process payment and generate paymentID
        if (paymentMethod.equals("Card")) {
            String details[] = paymentDetails.split(",");
            payment = new CardPayment(0, cost, LocalDateTime.now().toString(),
                    details[0], details[1], details[2]);
            this.paymentID = payment.getPaymentID();
        } else if (paymentMethod.equals("Cash")) {
            payment = new CashPayment(0, cost, LocalDateTime.now().toString());
            this.paymentID = payment.getPaymentID();
        } else {
            throw new IllegalArgumentException("Unsupported payment method.");
        }
        TicketPersistanceHandler tph = new TicketPersistanceHandler();
        this.ticketID = tph.save(this);

    }
    

    public Ticket(){}

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public void setIssueTime(LocalDateTime issueTime) {
        this.issueTime = issueTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTicketID() {
        return ticketID;
    }

    public LocalDateTime getIssueTime() {
        return issueTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public String getStatus() {
        return status;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }
    
    public boolean isValidTicket() {
        LocalDateTime now = LocalDateTime.now();

        return status.equals("Active") && now.isBefore(expiryTime);
    }

    public boolean isValidForCheckout() {
        LocalDateTime now = LocalDateTime.now();

        return status.equals("Used") && now.isBefore(expiryTime) && ride.isActive();
    }
}