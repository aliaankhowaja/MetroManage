package com.metromanage.domain;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.DB;
import com.metromanage.model.PassengerPersistanceHandler;
import com.metromanage.model.PaymentPersistanceHandler;
import com.metromanage.model.RidePersistanceHandler;
import com.metromanage.model.TicketPersistanceHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("StationRegister white-box tests (ticketing, check-in/out, wallet)")
class StationRegisterTest {

    // ---- helpers ------------------------------------------------------

    private static Route route(int id, float cost) {
        Route r = new Route();
        r.setRouteID(id);
        r.setCost(cost);
        r.setEstimatedTime(30);
        r.setActive(true);
        r.setRouteName("R" + id);
        return r;
    }

    private static Passenger passengerWithBalance(int id, Float wallet) {
        Passenger p = new Passenger();
        p.setPassengerID(id);
        p.setName("P");
        p.setEmail("p@x");
        p.setPhoneNumber("0");
        p.setPasswordHash("h");
        p.setStatus("Active");
        p.setWalletBalance(wallet);
        return p;
    }

    private static Bus bus(int id, String status, int routeID) {
        Bus b = new Bus();
        b.setBusID(id);
        b.setPlateNumber("P-" + id);
        b.setCapacity(40);
        b.setStatus(status);
        b.setRouteID(routeID);
        return b;
    }

    private static Ticket newBareTicket(int id, String status, LocalDateTime expiry) {
        Ticket t = new Ticket();
        t.setTicketID(id);
        t.setStatus(status);
        t.setIssueTime(LocalDateTime.now().minusMinutes(5));
        t.setExpiryTime(expiry);
        return t;
    }

    private static Ride newBareRide(int id, Route route, boolean active) {
        Ride r = new Ride();
        r.setRideID(id);
        r.setRoute(route);
        r.setActive(active);
        r.setBoardingStationID(1);
        return r;
    }

    // -----------------------------------------------------------------
    // requestTicket  (registered Passenger)
    // -----------------------------------------------------------------

    @Test
    @DisplayName("requestTicket(Wallet) succeeds when wallet balance is sufficient")
    void requestTicket_shouldSucceed_whenWalletHasEnough() {
        Route route = route(1, 30f);
        Passenger p = passengerWithBalance(7, 100f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class,
                             (m, ctx) -> when(m.savePayment(any(), anyString())).thenReturn(11));
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.save(any())).thenReturn(77));
             MockedConstruction<PassengerPersistanceHandler> pMc =
                     mockConstruction(PassengerPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.save(any())).thenReturn(77))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            Ticket t = reg.requestTicket(route, "Wallet", p, "", 1);

            assertNotNull(t);
            assertEquals("Active", t.getStatus());
            assertEquals(70f, p.getWalletBalance(), 0.0001f,
                    "wallet should be reduced by cost");
        }
    }

    @Test
    @DisplayName("requestTicket(Wallet) throws when wallet balance is insufficient")
    void requestTicket_shouldThrow_whenWalletTooLow() {
        Route route = route(1, 50f);
        Passenger p = passengerWithBalance(7, 10f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class);
             MockedConstruction<PassengerPersistanceHandler> pMc =
                     mockConstruction(PassengerPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> reg.requestTicket(route, "Wallet", p, "", 1));

            assertTrue(ex.getMessage().toLowerCase().contains("insufficient"));
            assertEquals(10f, p.getWalletBalance(), 0.0001f, "wallet must be unchanged");
        }
    }

    @Test
    @DisplayName("requestTicket(Card) succeeds with valid card details")
    void requestTicket_shouldSucceed_whenPaymentIsCard() {
        Route route = route(1, 40f);
        Passenger p = passengerWithBalance(7, 100f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class,
                             (m, ctx) -> when(m.savePayment(any(), anyString())).thenReturn(5));
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.save(any())).thenReturn(66));
             MockedConstruction<PassengerPersistanceHandler> pMc =
                     mockConstruction(PassengerPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            Ticket t = reg.requestTicket(route, "Card", p,
                    "4111111111111111,John Doe,12/29", 1);

            assertNotNull(t);
            assertEquals(100f, p.getWalletBalance(), 0.0001f,
                    "Card payment must not touch wallet balance");
        }
    }

    @Test
    @DisplayName("requestTicket(Cash) succeeds for a registered passenger")
    void requestTicket_shouldSucceed_whenPaymentIsCash() {
        Route route = route(1, 40f);
        Passenger p = passengerWithBalance(7, 100f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class,
                             (m, ctx) -> when(m.savePayment(any(), anyString())).thenReturn(3));
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class);
             MockedConstruction<PassengerPersistanceHandler> pMc =
                     mockConstruction(PassengerPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            Ticket t = reg.requestTicket(route, "Cash", p, "", 2);

            assertNotNull(t);
            assertEquals(100f, p.getWalletBalance(), 0.0001f);
        }
    }

    @Test
    @DisplayName("requestTicket throws for an unsupported payment method")
    void requestTicket_shouldThrow_whenPaymentMethodUnsupported() {
        Route route = route(1, 40f);
        Passenger p = passengerWithBalance(7, 100f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class);
             MockedConstruction<PassengerPersistanceHandler> pMc =
                     mockConstruction(PassengerPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            assertThrows(IllegalArgumentException.class,
                    () -> reg.requestTicket(route, "Crypto", p, "", 1));
        }
    }

    // -----------------------------------------------------------------
    // requestTicket (guest)
    // -----------------------------------------------------------------

    @Test
    @DisplayName("guest requestTicket(Card) succeeds")
    void requestTicket_guestCard_shouldSucceed() {
        Route route = route(2, 20f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class,
                             (m, ctx) -> when(m.savePayment(any(), anyString())).thenReturn(1));
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            Ticket t = reg.requestTicket(route, "Card",
                    "4111111111111111,Jane,01/30", 1);

            assertNotNull(t);
            assertEquals("Active", t.getStatus());
        }
    }

    @Test
    @DisplayName("guest requestTicket(Cash) succeeds")
    void requestTicket_guestCash_shouldSucceed() {
        Route route = route(2, 20f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class,
                             (m, ctx) -> when(m.savePayment(any(), anyString())).thenReturn(2));
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            Ticket t = reg.requestTicket(route, "Cash", "", 1);

            assertNotNull(t);
        }
    }

    @Test
    @DisplayName("guest requestTicket throws for an unsupported payment method (including Wallet)")
    void requestTicket_guestUnsupported_shouldThrow() {
        // The guest overload does NOT support Wallet; it is considered unsupported.
        Route route = route(2, 20f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PaymentPersistanceHandler> payMc =
                     mockConstruction(PaymentPersistanceHandler.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class);
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            StationRegister reg = new StationRegister();
            assertThrows(IllegalArgumentException.class,
                    () -> reg.requestTicket(route, "Wallet", "", 1));
        }
    }

    // -----------------------------------------------------------------
    // checkIn
    // -----------------------------------------------------------------

    @Test
    @DisplayName("checkIn returns safely when ticket is not found")
    void checkIn_shouldReturnSafely_whenTicketNotFound() {
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(anyInt())).thenReturn(null));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class);
             MockedConstruction<BusPersistanceHandler> bMc =
                     mockConstruction(BusPersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            assertDoesNotThrow(() -> new StationRegister().checkIn(1, 1, 1));
            verify(tMc.constructed().get(0), never()).save(any());
            // Ride and Bus lookups must not have been performed either.
            assertEquals(0, rMc.constructed().size(),
                    "RidePersistanceHandler should not be constructed on early return");
            assertEquals(0, bMc.constructed().size(),
                    "BusPersistanceHandler should not be constructed on early return");
        }
    }

    @Test
    @DisplayName("checkIn returns safely when ticket is invalid (expired or not Active)")
    void checkIn_shouldReturnSafely_whenTicketInvalid() {
        Ticket expired = newBareTicket(1, "Active", LocalDateTime.now().minusMinutes(1));

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(expired));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class);
             MockedConstruction<BusPersistanceHandler> bMc =
                     mockConstruction(BusPersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            assertDoesNotThrow(() -> new StationRegister().checkIn(1, 1, 1));
            verify(tMc.constructed().get(0), never()).save(any());
            assertEquals("Active", expired.getStatus(),
                    "expired ticket should not be flipped to Used");
        }
    }

    @Test
    @DisplayName("checkIn returns safely when bus route does not match ride route")
    void checkIn_shouldReturnSafely_whenBusRouteMismatch() {
        Ticket valid = newBareTicket(1, "Active", LocalDateTime.now().plusHours(1));
        Route rideRoute = route(5, 10f);
        Ride ride = newBareRide(1, rideRoute, true);
        Bus otherRouteBus = bus(1, "Active", 99);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(valid));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(ride));
             MockedConstruction<BusPersistanceHandler> bMc =
                     mockConstruction(BusPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(otherRouteBus))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            new StationRegister().checkIn(1, 1, 1);

            verify(tMc.constructed().get(0), never()).save(any());
            verify(rMc.constructed().get(0), never()).save(any());
            assertEquals("Active", valid.getStatus());
        }
    }

    @Test
    @DisplayName("checkIn returns safely when bus is Inactive")
    void checkIn_shouldReturnSafely_whenBusInactive() {
        Ticket valid = newBareTicket(1, "Active", LocalDateTime.now().plusHours(1));
        Route rideRoute = route(5, 10f);
        Ride ride = newBareRide(1, rideRoute, true);
        Bus inactiveBus = bus(1, "Inactive", 5);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(valid));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(ride));
             MockedConstruction<BusPersistanceHandler> bMc =
                     mockConstruction(BusPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(inactiveBus))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            new StationRegister().checkIn(1, 1, 1);

            verify(tMc.constructed().get(0), never()).save(any());
            verify(rMc.constructed().get(0), never()).save(any());
        }
    }

    @Test
    @DisplayName("checkIn updates ticket/ride and persists both on the happy path")
    void checkIn_shouldMarkUsedAndSave_whenAllValid() {
        Ticket valid = newBareTicket(1, "Active", LocalDateTime.now().plusHours(1));
        Route rideRoute = route(5, 10f);
        Ride ride = newBareRide(1, rideRoute, true);
        Bus activeBus = bus(1, "Active", 5);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(valid));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(ride));
             MockedConstruction<BusPersistanceHandler> bMc =
                     mockConstruction(BusPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(activeBus))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            new StationRegister().checkIn(1, 1, 9);

            assertEquals("Used", valid.getStatus());
            assertSame(activeBus, ride.getBus());
            assertEquals(9, ride.getBoardingStationID());
            assertNotNull(ride.getBoardingTime());
            verify(rMc.constructed().get(0)).save(ride);
            verify(tMc.constructed().get(0)).save(valid);
        }
    }

    @Test
    @DisplayName("DEFECT: checkIn throws NullPointerException when ride row is missing")
    void checkIn_shouldThrowNPE_whenRideIsNull() {
        // Defect: StationRegister.checkIn does not null-check rph.find(ticketId).
        // When the ride row is missing, ride.getRoute().getRouteID() NPEs.
        Ticket valid = newBareTicket(1, "Active", LocalDateTime.now().plusHours(1));

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(valid));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(null));
             MockedConstruction<BusPersistanceHandler> bMc =
                     mockConstruction(BusPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(bus(1, "Active", 5)))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            assertThrows(NullPointerException.class,
                    () -> new StationRegister().checkIn(1, 1, 1));
        }
    }

    @Test
    @DisplayName("DEFECT: checkIn throws NullPointerException when bus row is missing")
    void checkIn_shouldThrowNPE_whenBusIsNull() {
        // Defect: StationRegister.checkIn does not null-check bph.find(busId).
        // When the bus row is missing, bus.getRouteID() NPEs.
        Ticket valid = newBareTicket(1, "Active", LocalDateTime.now().plusHours(1));
        Route rideRoute = route(5, 10f);
        Ride ride = newBareRide(1, rideRoute, true);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(valid));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(ride));
             MockedConstruction<BusPersistanceHandler> bMc =
                     mockConstruction(BusPersistanceHandler.class,
                             (m, ctx) -> when(m.find(anyInt())).thenReturn(null))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            assertThrows(NullPointerException.class,
                    () -> new StationRegister().checkIn(1, 404, 1));
        }
    }

    // -----------------------------------------------------------------
    // checkOut
    // -----------------------------------------------------------------

    @Test
    @DisplayName("checkOut returns safely when ticket is not found")
    void checkOut_shouldReturnSafely_whenTicketNotFound() {
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(anyInt())).thenReturn(null));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class)) {
            dbMock.when(DB::getConnection).thenReturn(null);

            assertDoesNotThrow(() -> new StationRegister().checkOut(1, 1));
            assertEquals(0, rMc.constructed().size(),
                    "RidePersistanceHandler must not be constructed when ticket is missing");
        }
    }

    @Test
    @DisplayName("checkOut returns safely when ticket is not valid for checkout")
    void checkOut_shouldReturnSafely_whenTicketInvalidForCheckout() {
        // Ticket still Active (never checked in) => isValidForCheckout() == false.
        Ticket notUsed = newBareTicket(1, "Active", LocalDateTime.now().plusHours(1));
        Route r = route(5, 10f);
        Ride ride = newBareRide(1, r, true);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(notUsed));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(ride))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            new StationRegister().checkOut(1, 99);

            verify(rMc.constructed().get(0), never()).save(any());
            assertTrue(ride.isActive(), "ride must not be deactivated on invalid checkout");
        }
    }

    @Test
    @DisplayName("checkOut updates ride (arrival, inactive) and persists on the happy path")
    void checkOut_shouldUpdateAndSaveRide_whenValid() {
        Ticket used = newBareTicket(1, "Used", LocalDateTime.now().plusHours(1));
        Route r = route(5, 10f);
        Ride ride = newBareRide(1, r, true);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(used));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(ride))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            new StationRegister().checkOut(1, 21);

            assertEquals(21, ride.getArrivalStationID());
            assertNotNull(ride.getArrivalTime());
            assertFalse(ride.isActive(), "ride should be deactivated after checkout");
            verify(rMc.constructed().get(0)).save(ride);
        }
    }

    @Test
    @DisplayName("DEFECT: checkOut throws NullPointerException when ride row is missing")
    void checkOut_shouldThrowNPE_whenRideIsNull() {
        // Defect: StationRegister.checkOut does not null-check rph.find(ticketId),
        // and Ticket.isValidForCheckout() then dereferences ride.isActive().
        Ticket used = newBareTicket(1, "Used", LocalDateTime.now().plusHours(1));

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<TicketPersistanceHandler> tMc =
                     mockConstruction(TicketPersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(used));
             MockedConstruction<RidePersistanceHandler> rMc =
                     mockConstruction(RidePersistanceHandler.class,
                             (m, ctx) -> when(m.find(1)).thenReturn(null))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            assertThrows(NullPointerException.class,
                    () -> new StationRegister().checkOut(1, 5));
        }
    }

    // -----------------------------------------------------------------
    // checkBalance
    // -----------------------------------------------------------------

    @Test
    @DisplayName("checkBalance returns wallet balance for a known passenger")
    void checkBalance_shouldReturnBalance_whenPassengerFound() {
        Passenger p = passengerWithBalance(10, 123.45f);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> when(m.find(10)).thenReturn(p))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            float balance = new StationRegister().checkBalance(10);

            assertEquals(123.45f, balance, 0.0001f);
        }
    }

    @Test
    @DisplayName("checkBalance returns -1 when passenger is not found")
    void checkBalance_shouldReturnMinusOne_whenPassengerNotFound() {
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> when(m.find(anyInt())).thenReturn(null))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            float balance = new StationRegister().checkBalance(404);

            assertEquals(-1f, balance, 0.0001f);
        }
    }
}
