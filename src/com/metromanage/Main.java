package com.metromanage;

import com.metromanage.domain.Passenger;
import com.metromanage.domain.StationRegister;
import com.metromanage.domain.AdminRegister;
import com.metromanage.domain.Route;
import com.metromanage.model.DB;
import com.metromanage.model.PassengerPersistanceHandler;
import com.metromanage.model.RoutePersistanceHandler;
public class Main{
    public static void main(String[] args) {
        System.out.println("Welcome to MetroManage!");
        // Uses Windows Authentication
        // DB.createTables();
        generateTestData();
    }

    static void generateTestData(){
        // Station station1 = new Station("Central Station", 40.7128f, -74.0060f, "Active", DB.getConnection());
        // Station station2 = new Station("West End", 40.7138f, -74.0160f, "Active", DB.getConnection());
        // Station station3 = new Station("East Side", 40.7228f, -74.0010f, "Active", DB.getConnection());
        // Station station4 = new Station("North Point", 40.7328f, -74.0065f, "Active", DB.getConnection());
        // Station station5 = new Station("South Park", 40.7028f, -74.0060f, "Active", DB.getConnection());

        // Route route1 = new Route("Route A", 15.5f, 30, true, 2.50f, DB.getConnection());
        // Route route2 = new Route("Route B", 20.0f, 45, true, 3.00f, DB.getConnection());
        // Route route3 = new Route("Route C", 10.0f, 20, true, 1.75f, DB.getConnection());

        // Passenger passenger1 = new Passenger(1, "Alice Johnson", "alice.johnson@example.com", "555-1234");
        // passenger1.setWalletBalance(50.0f);

        StationRegister stationRegister = new StationRegister(DB.getConnection());
        AdminRegister adminRegister = new AdminRegister(DB.getConnection());


        // Passenger passenger1 = new Passenger("Alice Johnson", "alice.johnson@example.com", "555-1234", "passwordHash",
        //         "2024-06-01T12:00:00", 50.0f, DB.getConnection());
        // Passenger passenger2 = new Passenger("Bob Smith", "bob.smith@example.com", "555-5678", "passwordHash",
        //         "2024-06-02T13:30:00", 30.0f, DB.getConnection());
        // Passenger passenger3 = new Passenger("Charlie Brown", "charlie.brown@example.com", "555-9012", "passwordHash",
        //         "2024-06-03T15:45:00", 20.0f, DB.getConnection());

        Passenger passenger1 = adminRegister.addPassenger("ali", "aliaankhowaja@gmail.com", "+920900786012",
                "MetroManage@123", 50.0f);
        Passenger passenger2 = adminRegister.addPassenger("bob", "bob.smith@example.com", "555-5678", 
                "Pass123", 30.0f);
        System.out.println(Passenger.GenerateHash("MetroManage@123"));
        System.out.println(Passenger.GenerateHash("Pass123"));

        
        

        RoutePersistanceHandler rph = new RoutePersistanceHandler(DB.getConnection());
        Route route1 = (Route) rph.find(1);
        Route route2 = (Route) rph.find(2);
        Route route3 = (Route) rph.find(3);

        adminRegister.deletePassenger("bob.smith@example.com");


        // Bus bus1 = new Bus("APH-313", 40, "Active", 1, DB.getConnection());
        // Bus bus2 = new Bus("BPH-414", 50, "Active", 2, DB.getConnection());
        // Bus bus3 = new Bus("CPH-515", 30, "Active", 3, DB.getConnection());
        
        // register.requestTicket(route1, "Wallet", passenger1, "", DB.getConnection());
        // register.requestTicket(route2, "Card", "1234567890123456,Alice Johnson,12/25", DB.getConnection());
        // register.requestTicket(route3, "Wallet", passenger3, "", DB.getConnection());
        // register.checkIn(16, 3, DB.getConnection());
        // register.checkOut(13, DB.getConnection());
        // register.checkOut(16, DB.getConnection());


    }
}

