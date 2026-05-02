package com.metromanage.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.DB;
import com.metromanage.model.PassengerPersistanceHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("AdminRegister white-box tests (CRUD for Passenger and Bus)")
class AdminRegisterTest {

    /** Creates a Passenger instance without firing the DB-bound constructor. */
    private static Passenger passenger(int id, String email) {
        Passenger p = new Passenger();
        p.setPassengerID(id);
        p.setEmail(email);
        p.setName("old name");
        p.setPhoneNumber("old");
        p.setPasswordHash("old");
        p.setStatus("Active");
        p.setWalletBalance(10f);
        return p;
    }

    private static Bus bus(int id, String plate, String status, int routeId) {
        Bus b = new Bus();
        b.setBusID(id);
        b.setPlateNumber(plate);
        b.setCapacity(20);
        b.setStatus(status);
        b.setRouteID(routeId);
        return b;
    }

    // -----------------------------------------------------------------
    // addPassenger
    // -----------------------------------------------------------------

    @Test
    @DisplayName("addPassenger returns null when an account with the same email exists")
    void addPassenger_shouldReturnNull_whenEmailAlreadyExists() {
        Passenger existing = passenger(5, "dup@x.com");

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> when(m.findByEmail(anyString())).thenReturn(existing))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();
            Passenger result = reg.addPassenger("name", "dup@x.com", "123", "pw", 0f);

            assertNull(result, "Duplicate email must short-circuit and return null");
            verify(mc.constructed().get(0)).findByEmail("dup@x.com");
            // Ensure we did NOT try to persist a new passenger on the duplicate path.
            verify(mc.constructed().get(0), never()).save(any());
        }
    }

    @Test
    @DisplayName("addPassenger returns a new Passenger when the email is unique")
    void addPassenger_shouldReturnPassenger_whenEmailUnique() {
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> {
                         when(m.findByEmail(anyString())).thenReturn(null);
                         when(m.save(any())).thenReturn(99);
                     })) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();
            Passenger result = reg.addPassenger("Jane", "new@x.com", "555", "Secret!", 25f);

            assertNotNull(result);
            assertEquals("Jane", result.getName());
            assertEquals("new@x.com", result.getEmail());
            assertEquals(Passenger.GenerateHash("Secret!"), result.getPasswordHash(),
                    "Password should be stored as SHA-256 hash");
            assertEquals(25f, result.getWalletBalance(), 0.0001f);
            // ID came from the mocked PPH.save() in the Passenger constructor.
            assertEquals(99, result.getPassengerID());
        }
    }

    // -----------------------------------------------------------------
    // updatePassenger
    // -----------------------------------------------------------------

    @Test
    @DisplayName("updatePassenger updates fields and calls save when passenger exists")
    void updatePassenger_shouldUpdateAndSave_whenPassengerFound() {
        Passenger existing = passenger(3, "a@b");

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> when(m.findByEmail("a@b")).thenReturn(existing))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();
            reg.updatePassenger("a@b", "NewName", "999", "newPw", "Active", 42f);

            assertEquals("NewName", existing.getName());
            assertEquals("999", existing.getPhoneNumber());
            assertEquals(Passenger.GenerateHash("newPw"), existing.getPasswordHash());
            assertEquals("Active", existing.getStatus());
            assertEquals(42f, existing.getWalletBalance(), 0.0001f);
            verify(mc.constructed().get(0)).save(existing);
        }
    }

    @Test
    @DisplayName("DEFECT: updatePassenger throws NullPointerException when passenger not found")
    void updatePassenger_shouldThrowNPE_whenPassengerNotFound() {
        // Defect: AdminRegister.updatePassenger does not null-check the result
        // of pph.findByEmail, so it dereferences null on setName(...).
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> when(m.findByEmail(anyString())).thenReturn(null))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();

            assertThrows(NullPointerException.class,
                    () -> reg.updatePassenger("ghost@x", "N", "p", "pw", "Active", 0f));
        }
    }

    // -----------------------------------------------------------------
    // deletePassenger
    // -----------------------------------------------------------------

    @Test
    @DisplayName("deletePassenger marks the passenger deleted when found")
    void deletePassenger_shouldMarkDeleted_whenPassengerFound() {
        Passenger existing = passenger(4, "keep@x");

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> when(m.findByEmail("keep@x")).thenReturn(existing))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();
            reg.deletePassenger("keep@x");

            // markDeleted() constructs a second PPH and calls .delete(this).
            // So at least one constructed PPH should have seen a delete() call
            // with our passenger.
            boolean deleteObserved = mc.constructed().stream()
                    .anyMatch(m -> {
                        try {
                            verify(m).delete(existing);
                            return true;
                        } catch (AssertionError e) {
                            return false;
                        }
                    });
            assertTrue(deleteObserved, "Expected at least one PPH.delete(existing) call");
        }
    }

    @Test
    @DisplayName("deletePassenger safely no-ops when passenger is not found")
    void deletePassenger_shouldNotThrow_whenPassengerMissing() {
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<PassengerPersistanceHandler> mc = mockConstruction(
                     PassengerPersistanceHandler.class,
                     (m, ctx) -> when(m.findByEmail(anyString())).thenReturn(null))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();

            assertDoesNotThrow(() -> reg.deletePassenger("missing@x"));
            verify(mc.constructed().get(0), never()).delete(any());
        }
    }

    // -----------------------------------------------------------------
    // updateBus
    // -----------------------------------------------------------------

    @Test
    @DisplayName("updateBus updates fields and calls save when bus exists")
    void updateBus_shouldUpdateAndSave_whenBusFound() {
        Bus existing = bus(7, "OLD-1", "Active", 1);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<BusPersistanceHandler> mc = mockConstruction(
                     BusPersistanceHandler.class,
                     (m, ctx) -> when(m.find(7)).thenReturn(existing))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();
            reg.updateBus(7, "NEW-9", 40, "Inactive", 2);

            assertEquals("NEW-9", existing.getPlateNumber());
            assertEquals(40, existing.getCapacity());
            assertEquals("Inactive", existing.getStatus());
            assertEquals(2, existing.getRouteID());
            verify(mc.constructed().get(0)).save(existing);
        }
    }

    @Test
    @DisplayName("DEFECT: updateBus throws NullPointerException when bus is not found")
    void updateBus_shouldThrowNPE_whenBusNotFound() {
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<BusPersistanceHandler> mc = mockConstruction(
                     BusPersistanceHandler.class,
                     (m, ctx) -> when(m.find(anyInt())).thenReturn(null))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();

            assertThrows(NullPointerException.class,
                    () -> reg.updateBus(404, "X", 10, "Active", 1));
        }
    }

    // -----------------------------------------------------------------
    // deleteBus
    // -----------------------------------------------------------------

    @Test
    @DisplayName("deleteBus marks the bus deleted when found")
    void deleteBus_shouldMarkDeleted_whenBusFound() {
        Bus existing = bus(8, "P-1", "Active", 3);

        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<BusPersistanceHandler> mc = mockConstruction(
                     BusPersistanceHandler.class,
                     (m, ctx) -> when(m.find(8)).thenReturn(existing))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();
            reg.deleteBus(8);

            boolean deleteObserved = mc.constructed().stream()
                    .anyMatch(m -> {
                        try {
                            verify(m).delete(existing);
                            return true;
                        } catch (AssertionError e) {
                            return false;
                        }
                    });
            assertTrue(deleteObserved, "Expected at least one BPH.delete(existing) call");
        }
    }

    @Test
    @DisplayName("deleteBus safely no-ops when bus is not found")
    void deleteBus_shouldNotThrow_whenBusMissing() {
        try (MockedStatic<DB> dbMock = mockStatic(DB.class);
             MockedConstruction<BusPersistanceHandler> mc = mockConstruction(
                     BusPersistanceHandler.class,
                     (m, ctx) -> when(m.find(anyInt())).thenReturn(null))) {
            dbMock.when(DB::getConnection).thenReturn(null);

            AdminRegister reg = new AdminRegister();

            assertDoesNotThrow(() -> reg.deleteBus(999));
            verify(mc.constructed().get(0), never()).delete(any());
        }
    }
}
