package com.metromanage.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SessionManager white-box tests")
class SessionManagerTest {

    @BeforeEach
    void resetStaticSessionState() {
        // Reset both static fields between every test to guarantee isolation.
        SessionManager.logout();
    }

    @Test
    @DisplayName("initial state: no passenger and no admin are logged in")
    void sessionManager_shouldHaveNoSession_whenFreshlyReset() {
        assertNull(SessionManager.getLoggedInPassenger(),
                "Expected no passenger after reset");
        assertNull(SessionManager.getLoggedInAdmin(),
                "Expected no admin after reset");
        assertFalse(SessionManager.isLoggedIn(),
                "isLoggedIn should be false when no session exists");
        assertFalse(SessionManager.isAdminLoggedIn(),
                "isAdminLoggedIn should be false when no admin session exists");
    }

    @Test
    @DisplayName("createSession: stores passenger and flips isLoggedIn")
    void createSession_shouldStorePassenger_whenCalledWithPassenger() {
        Passenger passenger = new Passenger();
        passenger.setPassengerID(42);
        passenger.setName("Alice");

        SessionManager.createSession(passenger);

        assertSame(passenger, SessionManager.getLoggedInPassenger(),
                "Stored passenger reference should match the one created");
        assertTrue(SessionManager.isLoggedIn(),
                "isLoggedIn should return true after createSession");
        assertFalse(SessionManager.isAdminLoggedIn(),
                "Admin session should still be empty");
    }

    @Test
    @DisplayName("createAdminSession: stores admin and flips isAdminLoggedIn")
    void createAdminSession_shouldStoreAdmin_whenCalledWithAdmin() {
        Admin admin = new Admin();
        admin.setAdminID(7);
        admin.setName("Root");

        SessionManager.createAdminSession(admin);

        assertSame(admin, SessionManager.getLoggedInAdmin(),
                "Stored admin reference should match the one created");
        assertTrue(SessionManager.isAdminLoggedIn(),
                "isAdminLoggedIn should return true after createAdminSession");
        assertTrue(SessionManager.isLoggedIn(),
                "isLoggedIn returns true when either a passenger OR an admin is logged in");
    }

    @Test
    @DisplayName("logout: clears an existing passenger session")
    void logout_shouldClearPassengerSession_whenPassengerLoggedIn() {
        Passenger passenger = new Passenger();
        passenger.setPassengerID(1);
        SessionManager.createSession(passenger);
        assertTrue(SessionManager.isLoggedIn(), "precondition: passenger session is active");

        SessionManager.logout();

        assertNull(SessionManager.getLoggedInPassenger(), "passenger should be null after logout");
        assertFalse(SessionManager.isLoggedIn(), "isLoggedIn should be false after logout");
    }

    @Test
    @DisplayName("logout: clears an existing admin session")
    void logout_shouldClearAdminSession_whenAdminLoggedIn() {
        Admin admin = new Admin();
        admin.setAdminID(9);
        SessionManager.createAdminSession(admin);
        assertTrue(SessionManager.isAdminLoggedIn(), "precondition: admin session is active");

        SessionManager.logout();

        assertNull(SessionManager.getLoggedInAdmin(), "admin should be null after logout");
        assertFalse(SessionManager.isAdminLoggedIn(), "isAdminLoggedIn should be false after logout");
    }

    @Test
    @DisplayName("DEFECT NOTE: logout() is shared between passenger and admin and will clear both sessions")
    void logout_shouldClearBothSessions_documentingCrossContamination() {
        // This test documents an existing behaviour (potential defect):
        // SessionManager.logout() indiscriminately clears both sessions, so a
        // LoginHandler.logout() call by a passenger would also drop an admin
        // that happened to be logged in, and vice versa.
        Passenger passenger = new Passenger();
        Admin admin = new Admin();
        SessionManager.createSession(passenger);
        SessionManager.createAdminSession(admin);

        SessionManager.logout();

        assertNull(SessionManager.getLoggedInPassenger());
        assertNull(SessionManager.getLoggedInAdmin());
    }
}
