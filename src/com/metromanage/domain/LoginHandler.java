package com.metromanage.domain;

import com.metromanage.model.PassengerPersistanceHandler;
import com.metromanage.model.AdminPersistanceHandler;

public class LoginHandler {
    PassengerPersistanceHandler pph;
    AdminPersistanceHandler aph;

    public LoginHandler() {
        this.pph = new PassengerPersistanceHandler();
        this.aph = new AdminPersistanceHandler();
    }
    
    public int login(String email, String password) {
        // Status codes
        // 0 - Success
        // 1 - Already logged in
        // 2 - Invalid email
        // 3 - Invalid password

        
        if (SessionManager.isLoggedIn()) {
            System.out.println("A passenger is already logged in.");
            return 1;
        }
        Passenger passenger = (Passenger) pph.findByEmail(email);
        if(passenger == null) {
            System.out.println("Invalid email.");
            return 2;
        }
        if (passenger.getPasswordHash().equals(Passenger.GenerateHash(password))) {
            SessionManager.createSession(passenger);
            return 0;
        }
        System.out.println("Invalid email or password.");
        return 3;
    }

    public int logout() {
        // Status codes
        // 0 - Success
        // 1 - No passenger logged in
        if (!SessionManager.isLoggedIn()) {
            System.out.println("No passenger is currently logged in.");
            return 1;
        }
        SessionManager.logout();
        return 0;
    }

    public int adminLogin(String email, String password) {
        // Status codes
        // 0 - Success
        // 1 - Already logged in
        // 2 - Invalid email
        // 3 - Invalid password
        // 4 - Inactive or deleted account
        if (SessionManager.isAdminLoggedIn()) {
            return 1;
        }

        Admin admin = (Admin) aph.findByEmail(email == null ? null : email.trim());
        if (admin == null) {
            return 2;
        }

        if (!"Active".equalsIgnoreCase(admin.getStatus())) {
            return 4;
        }

        String storedPassword = admin.getPasswordHash();
        String hashedInput = Passenger.GenerateHash(password);
        boolean passwordMatches = password.equals(storedPassword) || hashedInput.equals(storedPassword);

        if (passwordMatches) {
            SessionManager.createAdminSession(admin);
            return 0;
        }

        return 3;
    }

    public int adminLogout() {
        if (!SessionManager.isAdminLoggedIn()) {
            return 1;
        }
        SessionManager.logout();
        return 0;
    }
}
