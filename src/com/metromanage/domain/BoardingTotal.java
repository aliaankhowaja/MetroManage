package com.metromanage.domain;

public class BoardingTotal {
    String stationName;
    String routeName;
    int totalBoardings;
    int date;
    int month;
    int year;
    
    public BoardingTotal(String stationName, String routeName, int totalBoardings, int date, int month, int year) {
        this.stationName = stationName;
        this.routeName = routeName;
        this.totalBoardings = totalBoardings;
        this.date = date;
        this.month = month;
        this.year = year;
    }
}
