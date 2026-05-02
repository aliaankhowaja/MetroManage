package com.metromanage.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.metromanage.model.AdminPersistanceHandler;
import com.metromanage.model.PassengerPersistanceHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LoginHandler white-box tests (admin login / logout)")
class LoginHandlerTest {

    private LoginHandler handler;
    private AdminPersistanceHandler aphMock;

    private Admin buildAdmin(String email, String hashedPassword, String status) {
        Admin a = new Admin();
        a.setAdminID(1);
        a.setName("Tester");
        a.setEmail(email);
        a.setPasswordHash(hashedPassword);
        a.setStatus(status);
        return a;
    }

    @BeforeEach
    void setUp() {
        // 1. Guarantee fresh session state before every test.
        SessionManager.logout();

        // 2. Build a LoginHandler whose internal persistence handlers are mocks,
        //    so no JDBC connection is attempted. mockConstruction intercepts the
        //    `new PassengerPersistanceHandler()` and `new AdminPersistanceHandler()`
        //    calls inside the LoginHandler() constructor.
        try (MockedConstruction<PassengerPersistanceHandler> mockedPph =
                     mockConstruction(PassengerPersistanceHandler.class);
             MockedConstruction<AdminPersistanceHandler> mockedAph =
                     mockConstruction(AdminPersistanceHandler.class)) {
            handler = new LoginHandler();
            aphMock = mockedAph.constructed().get(0);
            // Overwrite the package-private fields so post-block stubs on the same
            // mock instances continue to drive the LoginHandler's behaviour.
            handler.aph = aphMock;
            handler.pph = mockedPph.constructed().get(0);
        }
    }

    @Test
    @DisplayName("adminLogin returns 1 when an admin is already logged in")
    void adminLogin_shouldReturnOne_whenAdminAlreadyLoggedIn() {
        SessionManager.createAdminSession(buildAdmin("x@y", "h", "Active"));

        int result = handler.adminLogin("other@admin", "pw");

        assertEquals(1, result);
        verifyNoInteractions(aphMock); // short-circuit: no DB lookup attempted
    }

    @Test
    @DisplayName("adminLogin returns 2 when admin is not found for that email")
    void adminLogin_shouldReturnTwo_whenAdminNotFound() {
        when(aphMock.findByEmail("ghost@nope")).thenReturn(null);

        int result = handler.adminLogin("ghost@nope", "pw");

        assertEquals(2, result);
        assertFalse(SessionManager.isAdminLoggedIn());
    }

    @Test
    @DisplayName("adminLogin returns 4 when admin status is not Active")
    void adminLogin_shouldReturnFour_whenAdminStatusNotActive() {
        Admin inactive = buildAdmin("x@y", Passenger.GenerateHash("pw"), "Inactive");
        when(aphMock.findByEmail("x@y")).thenReturn(inactive);

        int result = handler.adminLogin("x@y", "pw");

        assertEquals(4, result);
        assertFalse(SessionManager.isAdminLoggedIn());
    }

    @Test
    @DisplayName("adminLogin returns 0 for a valid Active admin (hashed password match)")
    void adminLogin_shouldReturnZero_whenCredentialsValid() {
        String rawPassword = "Secret123";
        Admin active = buildAdmin("a@b", Passenger.GenerateHash(rawPassword), "Active");
        when(aphMock.findByEmail("a@b")).thenReturn(active);

        int result = handler.adminLogin("a@b", rawPassword);

        assertEquals(0, result);
        assertTrue(SessionManager.isAdminLoggedIn());
        assertSame(active, SessionManager.getLoggedInAdmin());
    }

    @Test
    @DisplayName("adminLogin returns 0 when stored password is plaintext (legacy branch)")
    void adminLogin_shouldReturnZero_whenStoredPasswordIsPlaintext() {
        // LoginHandler.adminLogin also accepts a plaintext compare against
        // the stored value: "password.equals(storedPassword)".
        Admin active = buildAdmin("c@d", "plainTextPw", "Active");
        when(aphMock.findByEmail("c@d")).thenReturn(active);

        int result = handler.adminLogin("c@d", "plainTextPw");

        assertEquals(0, result);
        assertTrue(SessionManager.isAdminLoggedIn());
    }

    @Test
    @DisplayName("adminLogin returns 3 for wrong password")
    void adminLogin_shouldReturnThree_whenPasswordMismatches() {
        Admin active = buildAdmin("a@b", Passenger.GenerateHash("correct"), "Active");
        when(aphMock.findByEmail("a@b")).thenReturn(active);

        int result = handler.adminLogin("a@b", "WRONG");

        assertEquals(3, result);
        assertFalse(SessionManager.isAdminLoggedIn());
    }

    @Test
    @DisplayName("adminLogout returns 1 when no admin is logged in")
    void adminLogout_shouldReturnOne_whenNoAdminSession() {
        int result = handler.adminLogout();

        assertEquals(1, result);
    }

    @Test
    @DisplayName("adminLogout returns 0 and clears the session when an admin was logged in")
    void adminLogout_shouldReturnZero_whenAdminSessionExists() {
        SessionManager.createAdminSession(buildAdmin("a@b", "h", "Active"));
        assertTrue(SessionManager.isAdminLoggedIn(), "precondition");

        int result = handler.adminLogout();

        assertEquals(0, result);
        assertFalse(SessionManager.isAdminLoggedIn());
        assertNull(SessionManager.getLoggedInAdmin());
    }
}
