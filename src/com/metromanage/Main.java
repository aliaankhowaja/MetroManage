package com.metromanage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main{
    public static void main(String[] args) {
        System.out.println("Welcome to MetroManage!");
        // Uses Windows Authentication
        String connectionUrl = "jdbc:sqlserver://localhost:55510;databaseName=MetroManage;encrypt=true;trustServerCertificate=true;integratedSecurity=true;";
        
        try (Connection conn = DriverManager.getConnection(connectionUrl)) {
            System.out.println("Connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}