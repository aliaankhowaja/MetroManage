package com.metromanage.domain;
import java.util.ArrayList;

import com.metromanage.model.StationPersistanceHandler;

public class Station{
    private int stationID;
    private String name;
    private float latitude;
    private float longitude;
    private String status;
    private ArrayList<String> routes;

    public Station(int stationID, String name, float latitude, float longitude, ArrayList<String> routes) {
        this.stationID = stationID;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routes = routes;
    }

    public Station(String name, float latitude, float longitude, String status) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        StationPersistanceHandler sph = new StationPersistanceHandler();
        this.stationID = sph.save(this);
    }

    public Station() {

    }
    
    public int getStationID() {
        return stationID;
    }

    public String getName() {
        return name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public ArrayList<String> getRoutes() {
        return routes;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setRoutes(ArrayList<String> routes) {
        this.routes = routes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}
