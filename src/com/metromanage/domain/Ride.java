package com.metromanage.domain;

import java.time.LocalDateTime;

import com.metromanage.model.RidePersistanceHandler;

public class Ride{
    private int rideID;
    private Route route;
    private Bus bus;
    private Ticket ticket;
    private LocalDateTime boardingTime;
    private LocalDateTime arrivalTime;
    private boolean isActive;

    private int boardingStationID;
    private int arrivalStationID;

    public Ride() {

    }

    public Ride(Route route, Ticket ticket, int boardingStationID, int arrivalStationID) {
        this.rideID = ticket.getTicketID();
        this.route = route;
        this.ticket = ticket;
        this.boardingStationID = boardingStationID;
        this.arrivalStationID = arrivalStationID;
        this.isActive = true;
        RidePersistanceHandler rph = new RidePersistanceHandler();
        this.rideID = rph.save(this);
    }

    public Ride(Route route, Ticket ticket, LocalDateTime boardingTime, LocalDateTime arrivalTime,
            int boardingStationID, int arrivalStationID) {
        this.rideID = ticket.getTicketID();
        this.route = route;
        this.ticket = ticket;
        this.boardingTime = boardingTime;
        this.arrivalTime = arrivalTime;
        this.boardingStationID = boardingStationID;
        this.arrivalStationID = arrivalStationID;
        this.isActive = true;
    }

    public Ride(Route route, Bus bus, Ticket ticket, LocalDateTime boardingTime, LocalDateTime arrivalTime,
            int boardingStationID, int arrivalStationID) {
        
                this.rideID = ticket.getTicketID();
        this.route = route;
        this.bus = bus;
        this.ticket = ticket;
        this.boardingTime = boardingTime;
        this.arrivalTime = arrivalTime;
        this.boardingStationID = boardingStationID;
        this.arrivalStationID = arrivalStationID;
        this.isActive = true;
    }
    
    public int getRideID() {
        return rideID;
    }

    public Route getRoute() {
        return route;
    }

    public Bus getBus() {
        return bus;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public LocalDateTime getBoardingTime() {
        return boardingTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public boolean isActive() {
        return isActive;
    }
    
    public void endRide() {
        this.isActive = false;
    }

    public int getBoardingStationID() {
        return boardingStationID;
    }

    public void setBoardingStationID(int boardingStationID) {
        this.boardingStationID = boardingStationID;
    }

    public int getArrivalStationID() {
        return arrivalStationID;
    }

    public void setArrivalStationID(int arrivalStationID) {
        this.arrivalStationID = arrivalStationID;
    }

    public void setRideID(int rideID) {
        this.rideID = rideID;
    }
    public void setRoute(Route route) {
        this.route = route;
    }
    public void setBus(Bus bus) {
        this.bus = bus;
    }
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
    public void setBoardingTime(LocalDateTime boardingTime) {
        this.boardingTime = boardingTime;
    }
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
}