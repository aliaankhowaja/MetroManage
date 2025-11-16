package com.metromanage.domain;

import java.sql.Connection;
import java.util.ArrayList;

import com.metromanage.model.RoutePersistanceHandler;
public class Route{
    private int routeID;
    private String name;
    private float totalDistance;
    private int estimatedTime;
    private boolean active;
    private float cost;
    ArrayList<Station> stations;
    ArrayList<Bus> buses;

    public Route(int routeID, String name, float totalDistance, int estimatedTime, boolean active, float cost,
            ArrayList<Station> stations, ArrayList<Bus> buses) {
        this.routeID = routeID;
        this.name = name;
        this.totalDistance = totalDistance;
        this.estimatedTime = estimatedTime;
        this.active = active;
        this.cost = cost;
        this.stations = new ArrayList<Station>();
        this.buses = new ArrayList<Bus>();
    }
    
    public Route(String name, float totalDistance, int estimatedTime, boolean active, float cost,
            Connection connection) {
        this.name = name;
        this.totalDistance = totalDistance;
        this.estimatedTime = estimatedTime;
        this.active = active;
        this.cost = cost;
        
        RoutePersistanceHandler rph = new RoutePersistanceHandler(connection);
        this.routeID = rph.save(this);
            
    }

    public float getCost() {
        return cost;
    }

    public int getRouteID() {
        return routeID;
    }

    public String getRouteName() {
        return name;
    }

    public float getTotalDistance() {
        return  totalDistance;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public boolean getActive() {
        return active;
    }
    
    public ArrayList<Station> getStations() {
        return stations;
    }

    public ArrayList<Bus> getBuses() {
        return buses;
    }

    public void setRouteName(String name) {
        this.name = name;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }


    public void addStation(Station station) {
        this.stations.add(station);
    }

    public void addBus(Bus bus) {
        this.buses.add(bus);
    }

    public void removeStation(Station station) {
        this.stations.remove(station);
    }

    public void removeBus(Bus bus) {
        this.buses.remove(bus);
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Route() {
    }

    
}