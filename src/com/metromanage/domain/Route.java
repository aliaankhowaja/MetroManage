package com.metromanage.domain;

import java.util.ArrayList;
public class Route{
    private String routeID;
    private String name;
    private int totalDistance;
    private String estimatedTime;
    private boolean isActive;
    ArrayList<Station> stations;
    ArrayList<Bus> buses;

}