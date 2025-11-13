package com.metromanage.domain;

public class Bus{
    private String busID;
    private String plateNumber;
    private int capacity;
    private String status;
    private String routeID;
    public Bus(String busID, String plateNumber, int capacity, String status, String routeID) {
        this.busID = busID;
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.status = status;
        this.routeID = routeID;
    }
    public void applyChanges(){

    }

    public void markDeleted(){

    }
    
}