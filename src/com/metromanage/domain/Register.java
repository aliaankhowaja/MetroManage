package com.metromanage.domain;

import java.sql.*;
import java.time.LocalDateTime;

import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.RidePersistanceHandler;
import com.metromanage.model.TicketPersistanceHandler;

public class Register {
    Connection dbConnection;
    public Register(Connection connection){
        this.dbConnection = connection;
    }

    public Ticket requestTicket(Route route, String paymentMethod, Passenger passenger, String paymentDetails, Connection connection){
        float cost = route.getCost();
        Ticket ticket = new Ticket(passenger, cost, paymentMethod, paymentDetails, connection);
        Ride ride = new Ride(route, ticket, connection);
        ticket.setRide(ride);
        return ticket;
    }

    public Ticket requestTicket(Route route, String paymentMethod, String paymentDetails, Connection connection) {
        float cost = route.getCost();
        Ticket ticket = new Ticket(cost, paymentMethod, paymentDetails, connection);
        Ride ride = new Ride(route, ticket, connection);
        ticket.setRide(ride);
        return ticket;
    }

    public void checkIn(int ticketId, int busId, Connection connection) {
        TicketPersistanceHandler tph = new TicketPersistanceHandler(connection);
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
        RidePersistanceHandler rph = new RidePersistanceHandler(connection);
        Ride ride = (Ride) rph.find(ticketId);
        BusPersistanceHandler bph = new BusPersistanceHandler(connection);
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
        rph.save(ride);
        tph.save(ticket);
        System.out.println("Check-in successful for Ticket ID: " + ticketId + " on Bus ID: " + busId);
    }
    public void checkOut(int ticketId, Connection connection) {
        TicketPersistanceHandler tph = new TicketPersistanceHandler(connection);
        Ticket ticket = (Ticket) tph.find(ticketId);
        if (ticket == null) {
            System.out.println("Invalid Ticket ID");
            return;
        }
        RidePersistanceHandler rph = new RidePersistanceHandler(connection);
        Ride ride = (Ride) rph.find(ticketId);
        ticket.setRide(ride);
        if (!ticket.isValidForCheckout()) {
            System.out.println("Ticket is not valid for checkout. Check-out failed.");
            return;
        }
        

        

        LocalDateTime now = LocalDateTime.now();
        ride.setArrivalTime(now);
        ride.setActive(false);
        rph.save(ride);
        System.out.println("Check-out successful for Ticket ID: " + ticketId);
    }
    
}