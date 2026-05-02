package com.metromanage.domain;

public class SessionManager {
    private static Passenger loggedInPassenger = null;
    private static Admin loggedInAdmin = null;

    public static Passenger getLoggedInPassenger() {
        return loggedInPassenger;
    }

    public static void setLoggedInPassenger(Passenger passenger) {
        loggedInPassenger = passenger;
    }

    public static boolean isLoggedIn() {
        return loggedInPassenger != null || loggedInAdmin != null;
    }

    public static void createSession(Passenger passenger) {
        loggedInPassenger = passenger;
    }

    public static void logout() {
        loggedInPassenger = null;
        loggedInAdmin = null;
    }

    public static void createAdminSession(Admin admin) {
        loggedInAdmin = admin;
    }

    public static Admin getLoggedInAdmin() {
        return loggedInAdmin;
    }

    public static boolean isAdminLoggedIn() {
        return loggedInAdmin != null;
    }
    
}
