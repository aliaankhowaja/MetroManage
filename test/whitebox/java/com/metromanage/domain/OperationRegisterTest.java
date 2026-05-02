package com.metromanage.domain;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.RoutePersistanceHandler;
import com.metromanage.model.StationPersistanceHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@DisplayName("OperationRegister white-box tests (boarding totals, allocation, schedule)")
class OperationRegisterTest {

    private static Bus bus(int id, String status, int routeId) {
        Bus b = new Bus();
        b.setBusID(id);
        b.setStatus(status);
        b.setRouteID(routeId);
        b.setPlateNumber("P-" + id);
        b.setCapacity(40);
        return b;
    }

    // -----------------------------------------------------------------
    // getBoardingTotals
    // -----------------------------------------------------------------

    @Test
    @DisplayName("getBoardingTotals('Daily') calls StationPersistanceHandler.getBoardingTotalsByDay")
    void getBoardingTotals_shouldCallDaily_whenDomainIsDaily() {
        ArrayList<BoardingTotal> expected = new ArrayList<>();
        expected.add(new BoardingTotal("S", "R", 10, 1, 1, 2024));

        try (MockedConstruction<StationPersistanceHandler> mc = mockConstruction(
                StationPersistanceHandler.class,
                (m, ctx) -> when(m.getBoardingTotalsByDay()).thenReturn(expected))) {

            ArrayList<BoardingTotal> result = new OperationRegister().getBoardingTotals("Daily");

            assertSame(expected, result);
            verify(mc.constructed().get(0)).getBoardingTotalsByDay();
            verify(mc.constructed().get(0), never()).getBoardingTotalsByMonth();
            verify(mc.constructed().get(0), never()).getBoardingTotalsByYear();
        }
    }

    @Test
    @DisplayName("getBoardingTotals('Monthly') calls the monthly path")
    void getBoardingTotals_shouldCallMonthly_whenDomainIsMonthly() {
        ArrayList<BoardingTotal> expected = new ArrayList<>();

        try (MockedConstruction<StationPersistanceHandler> mc = mockConstruction(
                StationPersistanceHandler.class,
                (m, ctx) -> when(m.getBoardingTotalsByMonth()).thenReturn(expected))) {

            ArrayList<BoardingTotal> result = new OperationRegister().getBoardingTotals("Monthly");

            assertSame(expected, result);
            verify(mc.constructed().get(0)).getBoardingTotalsByMonth();
            verify(mc.constructed().get(0), never()).getBoardingTotalsByDay();
        }
    }

    @Test
    @DisplayName("getBoardingTotals('Yearly') calls the yearly path")
    void getBoardingTotals_shouldCallYearly_whenDomainIsYearly() {
        ArrayList<BoardingTotal> expected = new ArrayList<>();

        try (MockedConstruction<StationPersistanceHandler> mc = mockConstruction(
                StationPersistanceHandler.class,
                (m, ctx) -> when(m.getBoardingTotalsByYear()).thenReturn(expected))) {

            ArrayList<BoardingTotal> result = new OperationRegister().getBoardingTotals("Yearly");

            assertSame(expected, result);
            verify(mc.constructed().get(0)).getBoardingTotalsByYear();
        }
    }

    @Test
    @DisplayName("getBoardingTotals(invalid) returns null and does not delegate")
    void getBoardingTotals_shouldReturnNull_whenDomainUnknown() {
        // NOTE: passing null here would NPE in production ("timeDomain.equals(...)").
        // The required case uses a non-null unrecognised value.
        try (MockedConstruction<StationPersistanceHandler> mc = mockConstruction(
                StationPersistanceHandler.class)) {

            ArrayList<BoardingTotal> result = new OperationRegister().getBoardingTotals("Weekly");

            assertNull(result);
            verifyNoInteractions(mc.constructed().get(0));
        }
    }

    // -----------------------------------------------------------------
    // allocateBusToRoute
    // -----------------------------------------------------------------

    @Test
    @DisplayName("allocateBusToRoute does nothing when bus ID is invalid")
    void allocateBusToRoute_shouldNoOp_whenBusNotFound() {
        try (MockedConstruction<BusPersistanceHandler> mc = mockConstruction(
                BusPersistanceHandler.class,
                (m, ctx) -> when(m.find(anyInt())).thenReturn(null))) {

            assertDoesNotThrow(() -> new OperationRegister().allocateBusToRoute(404, 1));

            verify(mc.constructed().get(0), never()).save(any());
        }
    }

    @Test
    @DisplayName("allocateBusToRoute updates routeID and saves for a valid bus")
    void allocateBusToRoute_shouldUpdateRouteAndSave_whenBusFound() {
        Bus existing = bus(3, "Active", 0);

        try (MockedConstruction<BusPersistanceHandler> mc = mockConstruction(
                BusPersistanceHandler.class,
                (m, ctx) -> when(m.find(3)).thenReturn(existing))) {

            new OperationRegister().allocateBusToRoute(3, 17);

            assertEquals(17, existing.getRouteID());
            verify(mc.constructed().get(0)).save(existing);
        }
    }

    // -----------------------------------------------------------------
    // getSchedule
    // -----------------------------------------------------------------

    @Test
    @DisplayName("getSchedule returns -1 when route cannot be resolved")
    void getSchedule_shouldReturnMinusOne_whenRouteMissing() {
        ArrayList<Bus> anyBuses = new ArrayList<>();
        anyBuses.add(bus(1, "Active", 5));

        try (MockedConstruction<BusPersistanceHandler> mcBus = mockConstruction(
                BusPersistanceHandler.class,
                (m, ctx) -> when(m.getActiveBusesForRoute(anyInt())).thenReturn(anyBuses));
             MockedConstruction<RoutePersistanceHandler> mcRoute = mockConstruction(
                     RoutePersistanceHandler.class,
                     (m, ctx) -> when(m.find(anyInt())).thenReturn(null))) {

            int result = new OperationRegister().getSchedule(5);

            assertEquals(-1, result);
        }
    }

    @Test
    @DisplayName("getSchedule returns -1 when no active buses exist on the route")
    void getSchedule_shouldReturnMinusOne_whenNoActiveBuses() {
        Route route = new Route();
        route.setRouteID(5);
        route.setEstimatedTime(60);

        try (MockedConstruction<BusPersistanceHandler> mcBus = mockConstruction(
                BusPersistanceHandler.class,
                (m, ctx) -> when(m.getActiveBusesForRoute(anyInt())).thenReturn(new ArrayList<>()));
             MockedConstruction<RoutePersistanceHandler> mcRoute = mockConstruction(
                     RoutePersistanceHandler.class,
                     (m, ctx) -> when(m.find(5)).thenReturn(route))) {

            int result = new OperationRegister().getSchedule(5);

            assertEquals(-1, result);
        }
    }

    @Test
    @DisplayName("getSchedule returns estimatedTime / busCount for a valid route with active buses")
    void getSchedule_shouldReturnInterval_whenRouteAndBusesValid() {
        Route route = new Route();
        route.setRouteID(5);
        route.setEstimatedTime(60); // minutes

        ArrayList<Bus> active = new ArrayList<>();
        active.add(bus(1, "Active", 5));
        active.add(bus(2, "Active", 5));
        active.add(bus(3, "Active", 5));

        try (MockedConstruction<BusPersistanceHandler> mcBus = mockConstruction(
                BusPersistanceHandler.class,
                (m, ctx) -> when(m.getActiveBusesForRoute(5)).thenReturn(active));
             MockedConstruction<RoutePersistanceHandler> mcRoute = mockConstruction(
                     RoutePersistanceHandler.class,
                     (m, ctx) -> when(m.find(5)).thenReturn(route))) {

            int result = new OperationRegister().getSchedule(5);

            assertEquals(20, result, "60 minutes / 3 buses = 20 minutes interval");
        }
    }

    // -----------------------------------------------------------------
    // getPeakHours
    // -----------------------------------------------------------------

    @Test
    @DisplayName("getPeakHours delegates to StationPersistanceHandler.getBoardingTotalsByHour")
    void getPeakHours_shouldDelegateToStationHandler_whenCalled() {
        ArrayList<BoardingTotal> expected = new ArrayList<>();
        expected.add(new BoardingTotal("S", "R", 5, 8, 0, 0));

        try (MockedConstruction<StationPersistanceHandler> mc = mockConstruction(
                StationPersistanceHandler.class,
                (m, ctx) -> when(m.getBoardingTotalsByHour(11)).thenReturn(expected))) {

            ArrayList<BoardingTotal> result = new OperationRegister().getPeakHours(11);

            assertSame(expected, result);
            verify(mc.constructed().get(0)).getBoardingTotalsByHour(11);
        }
    }
}
