package com.metromanage.domain;

import java.sql.*;
import java.time.LocalDateTime;

import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.DB;
import com.metromanage.model.PassengerPersistanceHandler;
import com.metromanage.model.RidePersistanceHandler;
import com.metromanage.model.TicketPersistanceHandler;

public class StationRegister {
    Connection dbConnection;
    public StationRegister(){
        this.dbConnection = DB.getConnection();
    }

    public Ticket requestTicket(Route route, String paymentMethod, Passenger passenger, String paymentDetails, int boardingStationID){
        float cost = route.getCost();
        Ticket ticket = new Ticket(passenger, cost, paymentMethod, paymentDetails);
        Ride ride = new Ride(route, ticket, boardingStationID, 0);
        ticket.setRide(ride);
        return ticket;
    }

    public Ticket requestTicket(Route route, String paymentMethod, String paymentDetails, int boardingStationID) {
        float cost = route.getCost();
        Ticket ticket = new Ticket(cost, paymentMethod, paymentDetails);
        Ride ride = new Ride(route, ticket, boardingStationID, 0);
        ticket.setRide(ride);
        return ticket;
    }

    public void checkIn(int ticketId, int busId, int boardingStationID) {
        TicketPersistanceHandler tph = new TicketPersistanceHandler();
        Ticket ticket = (Ticket) tph.find(ticketId);
        if (ticket == null) {
            System.out.println("Invalid Ticket ID");
            return;
        }
        boolean valid = ticket.isValidTicket();
        if (!valid) {
            System.out.println("Ticket is invalid or expired. Check-in failed.");
            return;
        }
        RidePersistanceHandler rph = new RidePersistanceHandler();
        Ride ride = (Ride) rph.find(ticketId);
        BusPersistanceHandler bph = new BusPersistanceHandler();
        Bus bus = (Bus) bph.find(busId);
        if (ride.getRoute().getRouteID() != bus.getRouteID()) {
            System.out.println("Bus does not serve the route of the ticket. Check-in failed.");
            return;
        }
        if(bus.getStatus().equals("Inactive")){
            System.out.println("Bus is inactive. Check-in failed.");
            return;
        }
        ticket.setStatus("Used"); // Mark ticket as used
        LocalDateTime now = LocalDateTime.now();
        ride.setBoardingTime(now);
        ride.setBus(bus);
        ride.setBoardingStationID(boardingStationID);
        rph.save(ride);
        tph.save(ticket);
        System.out.println("Check-in successful for Ticket ID: " + ticketId + " on Bus ID: " + busId + " at Station ID: " + boardingStationID);
    }

    public void checkOut(int ticketId, int arrivalStationID) {
        TicketPersistanceHandler tph = new TicketPersistanceHandler();
        Ticket ticket = (Ticket) tph.find(ticketId);
        if (ticket == null) {
            System.out.println("Invalid Ticket ID");
            return;
        }
        RidePersistanceHandler rph = new RidePersistanceHandler();
        Ride ride = (Ride) rph.find(ticketId);
        ticket.setRide(ride);
        if (!ticket.isValidForCheckout()) {
            System.out.println("Ticket is not valid for checkout. Check-out failed.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        ride.setArrivalTime(now);
        ride.setArrivalStationID(arrivalStationID);
        ride.setActive(false);
        rph.save(ride);
        System.out.println("Check-out successful for Ticket ID: " + ticketId + " at Station ID: " + arrivalStationID);
    }
    
    public void submitFeedback(int passengerID, String type, String comments) {
        Feedback feedback = new Feedback(passengerID, type, comments);
        System.out.println("Feedback submitted successfully with ID: " + feedback.getFeedbackID());
    }
    public float checkBalance(int passengerID) {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
        Passenger passenger = (Passenger) pph.find(passengerID);
        if (passenger != null) {
            return passenger.getWalletBalance();
        } else {
            System.out.println("Passenger not found.");
            return -1;
        }
    }
    
}