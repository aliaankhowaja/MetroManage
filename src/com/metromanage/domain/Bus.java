package com.metromanage.domain;

import com.metromanage.model.BusPersistanceHandler;

public class Bus{
    private int busID;
    private String plateNumber;
    private int capacity;
    private String status;
    private int routeID;

    public Bus(int busID, String plateNumber, int capacity, String status, int routeID) {
        this.busID = busID;
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.status = status;
        this.routeID = routeID;
    }
    
    public Bus(String plateNumber, int capacity, String status, int routeID) {
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.status = status;
        this.routeID = routeID;
        BusPersistanceHandler bph = new BusPersistanceHandler();
        this.busID = bph.save(this);
    }

    public void applyChanges(){

    }

    public void markDeleted() {

    }
    
    public int getBusID() {
        return busID;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getStatus() {
        return status;
    }
    
    public int getRouteID() {
        return routeID;
    }

    public void setBusID(int busID) {
        this.busID = busID;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }
    public Bus(){}
}