package com.metromanage.domain;

public class SessionManager {
    private static Passenger loggedInPassenger = null;

    public static Passenger getLoggedInPassenger() {
        return loggedInPassenger;
    }

    public static void setLoggedInPassenger(Passenger passenger) {
        loggedInPassenger = passenger;
    }

    public static boolean isLoggedIn() {
        return loggedInPassenger != null;
    }

    public static void createSession(Passenger passenger) {
        loggedInPassenger = passenger;
    }

    public static void logout() {
        loggedInPassenger = null;
    }
    
}
