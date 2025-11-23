package com.metromanage;

import com.metromanage.domain.StationRegister;
import com.metromanage.model.PassengerPersistanceHandler;
import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.RoutePersistanceHandler;
import com.metromanage.model.DB;
import com.metromanage.domain.AdminRegister;
import com.metromanage.domain.LoginHandler;
import com.metromanage.domain.OperationRegister;
import com.metromanage.domain.Passenger;
import com.metromanage.domain.Bus;
import com.metromanage.domain.Route;
public class Main{
    public static void main(String[] args) {
        System.out.println("Welcome to MetroManage!");
        // Uses Windows Authentication
        //DB.createTables();
        //generateTestData();
        //loginTest();
        //feedbackTest();
        //balanceTest();
        //manageBusTest();
        //allocateBusTest();
        searchPassengerTest();
        searchBusTest();
        // DB.createTables();
        // generateTestData();
        // loginTest();
        // feedbackTest();
        // balanceTest();
        // manageBusTest();
        // allocateBusTest();
        scheduleTest();
    }

    static void requestTicketTest() {
        StationRegister stationRegister = new StationRegister();
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
        RoutePersistanceHandler rph = new RoutePersistanceHandler();
        Route route1 = (Route) rph.find(1);
        
        Passenger passenger = (Passenger) pph.find(5);
        stationRegister.requestTicket(route1, "Wallet", passenger, "", 1);

        
    }

    static void generateTestData() {
        // StationRegister stationRegister = new StationRegister();
        // AdminRegister adminRegister = new AdminRegister();
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

        // Passenger passenger1 = new Passenger("Alice Johnson", "alice.johnson@example.com", "555-1234", "passwordHash",
        //         "2024-06-01T12:00:00", 50.0f, DB.getConnection());
        // Passenger passenger2 = new Passenger("Bob Smith", "bob.smith@example.com", "555-5678", "passwordHash",
        //         "2024-06-02T13:30:00", 30.0f, DB.getConnection());
        // Passenger passenger3 = new Passenger("Charlie Brown", "charlie.brown@example.com", "555-9012", "passwordHash",
        //         "2024-06-03T15:45:00", 20.0f, DB.getConnection());

        // Passenger passenger1 = adminRegister.addPassenger("ali", "aliaankhowaja@gmail.com", "+920900786012",
        //         "MetroManage@123", 50.0f);
        // Passenger passenger2 = adminRegister.addPassenger("bob", "bob.smith@example.com", "555-5678", 
        //         "Pass123", 30.0f);
        // System.out.println(Passenger.GenerateHash("MetroManage@123"));
        // System.out.println(Passenger.GenerateHash("Pass123"));

        // RoutePersistanceHandler rph = new RoutePersistanceHandler(DB.getConnection());
        // Route route1 = (Route) rph.find(1);
        // Route route2 = (Route) rph.find(2);
        // Route route3 = (Route) rph.find(3);

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
    


    static void loginTest() {
        LoginHandler loginHandler = new LoginHandler();
        loginHandler.logout();
        loginHandler.login("aliaankhowaja@gmail.com", "MetroManage@123");
        loginHandler.login("Dfsf", "sdfsdf");
        loginHandler.logout();
        loginHandler.login("SDF", "DFs");

    }
    
    static void feedbackTest() {
        StationRegister feedbackRegister = new StationRegister();
        feedbackRegister.submitFeedback(1, "Complaint", "The bus was late.");
        feedbackRegister.submitFeedback(2, "Suggestion", "Add more buses during peak hours.");
    }

    static void balanceTest() {
        StationRegister stationRegister = new StationRegister();
        float balance = stationRegister.checkBalance(1);
        System.out.println("Passenger 1 wallet balance: " + balance);
        balance = stationRegister.checkBalance(7);
        System.out.println("Passenger 7 wallet balance: " + balance);

    }
    


    static void manageBusTest() {
        AdminRegister adminRegister = new AdminRegister();
        adminRegister.deleteBus(2);
        adminRegister.updateBus(1, "APH-999", 45, "Active", 1);
        adminRegister.addBus("CPH-515", 30, "Active", 3);
        adminRegister.addBus("CPH-515", 30, "Active", 3);
        adminRegister.deleteBus(5);
    }

    static void allocateBusTest() {
        OperationRegister operationRegister = new OperationRegister();
        operationRegister.allocateBusToRoute(1, 2);
    }

    static void searchPassengerTest() {
        PassengerPersistanceHandler pph = new PassengerPersistanceHandler();
               
        //Search by partial name
        System.out.println("1. Searching for 'ali':");
        java.util.ArrayList<Passenger> results = pph.searchPassengers("ali", false);
        displaySearchResults(results);
        
        //Search by email
        System.out.println("\n2. Searching for 'gmail':");
        results = pph.searchPassengers("gmail", false);
        displaySearchResults(results);
        
        //Search by phone
        System.out.println("\n3. Searching for '555':");
        results = pph.searchPassengers("555", false);
        displaySearchResults(results);
        
        //Get all passengers
        System.out.println("\n4. Getting all active passengers:");
        java.util.ArrayList<Passenger> allPassengers = pph.getAllPassengers();
        displaySearchResults(allPassengers);
        
        //Search with no results
        System.out.println("\n5. Searching for 'xyz123notfound':");
        results = pph.searchPassengers("xyz123notfound", false);
        displaySearchResults(results);
    }
    
    static void displaySearchResults(java.util.ArrayList<Passenger> passengers) {
        if (passengers.isEmpty()) {
            System.out.println("   No passengers found.");
        } else {
            System.out.println("   Found " + passengers.size() + " passenger(s):");
            System.out.println("   " + "-".repeat(80));
            System.out.printf("   %-5s %-20s %-30s %-15s %-10s%n", 
                "ID", "Name", "Email", "Phone", "Balance");
            System.out.println("   " + "-".repeat(80));
            
            for (Passenger p : passengers) {
                System.out.printf("   %-5d %-20s %-30s %-15s $%-9.2f%n",
                    p.getPassengerID(),
                    p.getName(),
                    p.getEmail(),
                    p.getPhoneNumber(),
                    p.getWalletBalance()
                );
            }
            System.out.println("   " + "-".repeat(80));
        }
    }

    static void searchBusTest() {
        BusPersistanceHandler bph = new BusPersistanceHandler();
        
        // Search by plate number
        System.out.println("1. Searching for 'APH':");
        java.util.ArrayList<Bus> results = bph.searchBuses("APH", false);
        displayBusSearchResults(results);
        
        // Search by status
        System.out.println("\n2. Searching for 'Active':");
        results = bph.searchBuses("Active", false);
        displayBusSearchResults(results);
        
        // Search by capacity
        System.out.println("\n3. Searching for '40':");
        results = bph.searchBuses("40", false);
        displayBusSearchResults(results);
        
        // Get all buses
        System.out.println("\n4. Getting all active buses:");
        java.util.ArrayList<Bus> allBuses = bph.getAllBuses();
        displayBusSearchResults(allBuses);
        
        // Search with no results
        System.out.println("\n5. Searching for 'XYZ999':");
        results = bph.searchBuses("XYZ999", false);
        displayBusSearchResults(results);
    }
    
    static void displayBusSearchResults(java.util.ArrayList<Bus> buses) {
        if (buses.isEmpty()) {
            System.out.println("   No buses found.");
        } else {
            System.out.println("   Found " + buses.size() + " bus(es):");
            System.out.println("   " + "-".repeat(70));
            System.out.printf("   %-5s %-15s %-10s %-15s %-10s%n",
                    "ID", "Plate Number", "Capacity", "Status", "Route ID");
            System.out.println("   " + "-".repeat(70));

            for (Bus b : buses) {
                System.out.printf("   %-5d %-15s %-10d %-15s %-10d%n",
                        b.getBusID(),
                        b.getPlateNumber(),
                        b.getCapacity(),
                        b.getStatus(),
                        b.getRouteID());
            }
            System.out.println("   " + "-".repeat(70));
        }
    }

    static void scheduleTest() {
        OperationRegister operationRegister = new OperationRegister();
        int interval = operationRegister.getSchedule(2);
        System.out.println("Next bus interval for route 2: " + interval + " minutes");
        interval = operationRegister.getSchedule(3);
        System.out.println("Next bus interval for route 3: " + interval + " minutes");
        
    }
}

