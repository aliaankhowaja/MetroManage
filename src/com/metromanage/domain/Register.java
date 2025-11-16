package com.metromanage.domain;

import java.sql.*;

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
    public Ticket requestTicket(Route route, String paymentMethod, String paymentDetails, Connection connection){
        float cost = route.getCost();
        Ticket ticket = new Ticket(cost, paymentMethod, paymentDetails, connection);
        Ride ride = new Ride(route, ticket, connection);
        ticket.setRide(ride);
        return ticket;
    }
    
}