package com.metromanage.domain;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ticket white-box tests (pure logic)")
class TicketTest {

    /** Builds a ticket without going through the DB-coupled constructors. */
    private static Ticket newTicket(String status, LocalDateTime expiry) {
        Ticket t = new Ticket();
        t.setStatus(status);
        t.setIssueTime(LocalDateTime.now().minusMinutes(1));
        t.setExpiryTime(expiry);
        return t;
    }

    @Test
    @DisplayName("isValidTicket: true for Active and not-expired")
    void isValidTicket_shouldReturnTrue_whenActiveAndNotExpired() {
        Ticket t = newTicket("Active", LocalDateTime.now().plusHours(1));

        assertTrue(t.isValidTicket());
    }

    @Test
    @DisplayName("isValidTicket: false when status is not Active")
    void isValidTicket_shouldReturnFalse_whenStatusIsNotActive() {
        Ticket t = newTicket("Used", LocalDateTime.now().plusHours(1));

        assertFalse(t.isValidTicket());
    }

    @Test
    @DisplayName("isValidTicket: false when expired")
    void isValidTicket_shouldReturnFalse_whenExpired() {
        Ticket t = newTicket("Active", LocalDateTime.now().minusMinutes(1));

        assertFalse(t.isValidTicket());
    }

    @Test
    @DisplayName("isValidForCheckout: true when Used, not expired, and ride active")
    void isValidForCheckout_shouldReturnTrue_whenUsedAndRideActive() {
        Ticket t = newTicket("Used", LocalDateTime.now().plusHours(1));
        Ride ride = new Ride();
        ride.setActive(true);
        t.setRide(ride);

        assertTrue(t.isValidForCheckout());
    }

    @Test
    @DisplayName("isValidForCheckout: false when ride is inactive")
    void isValidForCheckout_shouldReturnFalse_whenRideIsInactive() {
        Ticket t = newTicket("Used", LocalDateTime.now().plusHours(1));
        Ride ride = new Ride();
        ride.setActive(false);
        t.setRide(ride);

        assertFalse(t.isValidForCheckout());
    }

    @Test
    @DisplayName("isValidForCheckout: false when status still Active (never checked in)")
    void isValidForCheckout_shouldReturnFalse_whenStatusIsStillActive() {
        Ticket t = newTicket("Active", LocalDateTime.now().plusHours(1));
        Ride ride = new Ride();
        ride.setActive(true);
        t.setRide(ride);

        assertFalse(t.isValidForCheckout());
    }

    @Test
    @DisplayName("DEFECT NOTE: isValidForCheckout throws NPE when ride is null")
    void isValidForCheckout_shouldThrowNPE_whenRideIsNull() {
        // Documents a null-safety hole: if StationRegister.checkOut is invoked
        // with a ticketId whose ride row is missing, ticket.setRide(null) is
        // called and then isValidForCheckout() dereferences ride.isActive().
        Ticket t = newTicket("Used", LocalDateTime.now().plusHours(1));

        assertThrows(NullPointerException.class, t::isValidForCheckout);
    }
}
