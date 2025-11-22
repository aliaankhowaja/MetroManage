package com.metromanage.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    static Connection dbConnection;

    private DB() {}

    public static Connection getConnection() {
        if(dbConnection == null) {
            String connectionUrl = "jdbc:sqlserver://localhost:55510;databaseName=MetroManage;encrypt=false;trustServerCertificate=true;integratedSecurity=true;";
            try {
                dbConnection = DriverManager.getConnection(connectionUrl);
                System.out.println("Connected successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Created db connection");
            return dbConnection;
        }else {
            return dbConnection;
        }
    }
    
    public static void createTables() {
        /*
            ride(id, ticketIssueTime, ticketExpiryTime, ticketStatus, routeID, busID, boardingTime, arrivalTime, isActive)
            route(id, routeName, totalDistance, estimatedTime, isActive, cost)
            payment(id, PassengerID, Amount, PaymentDate, CardNumber, CardHolderName, CardExpiryDate, PaymentType)
            bus(id, plateNumber, capacity, status, routeID)
            RouteStation(stationID, routeID)
            Station(id, name, latitude, longitude, status)  
        */

        String createRideTable = "CREATE TABLE ride (id INT PRIMARY KEY IDENTITY(1,1), ticketIssueTime DATETIME, ticketExpiryTime DATETIME, ticketStatus VARCHAR(50), paymentID INT, routeID INT, busID INT, boardingTime DATETIME, arrivalTime DATETIME, isActive BIT, boardingStationID INT, arrivalStationID INT)";
        String createRouteTable = "CREATE TABLE route (id INT PRIMARY KEY IDENTITY(1,1), routeName VARCHAR(100), totalDistance FLOAT, estimatedTime INT, isActive BIT, cost FLOAT)";
        String createPaymentTable = "CREATE TABLE payment (id INT PRIMARY KEY IDENTITY(1,1), PassengerID INT, Amount FLOAT, PaymentDate DATETIME, CardNumber VARCHAR(20), CardHolderName VARCHAR(100), CardExpiryDate VARCHAR(10), PaymentType VARCHAR(50))";
        String createBusTable = "CREATE TABLE bus (id INT PRIMARY KEY IDENTITY(1,1), plateNumber VARCHAR(20), capacity INT, status VARCHAR(50), routeID INT)";
        String createRouteStationTable = "CREATE TABLE RouteStation (stationID INT, routeID INT)";
        String createStationTable = "CREATE TABLE Station (id INT PRIMARY KEY IDENTITY(1,1), name VARCHAR(100), latitude FLOAT, longitude FLOAT, status VARCHAR(50))";
        String createPassengerTable = "CREATE TABLE Passenger (passengerID INT PRIMARY KEY IDENTITY(1,1), name VARCHAR(100), email VARCHAR(100), phoneNumber VARCHAR(15), passwordHash VARCHAR(255), status VARCHAR(50), registrationDate DATETIME, walletBalance FLOAT)";
        String createFeedbackTable = "CREATE TABLE Feedback (feedbackID INT PRIMARY KEY IDENTITY(1,1), passengerID INT, type VARCHAR(50), comments VARCHAR(255), timestamp DATETIME)";
        
        if(dbConnection == null) {
            getConnection();
        }

        try (Statement stmt = dbConnection.createStatement()) {
            stmt.execute(createRideTable);
            stmt.execute(createRouteTable);
            stmt.execute(createPaymentTable);
            stmt.execute(createBusTable);
            stmt.execute(createRouteStationTable);
            stmt.execute(createStationTable);
            stmt.execute(createPassengerTable);
            stmt.execute(createFeedbackTable);
            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
