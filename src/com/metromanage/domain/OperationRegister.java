package com.metromanage.domain;

import java.util.ArrayList;

import com.metromanage.model.BusPersistanceHandler;
import com.metromanage.model.StationPersistanceHandler;

public class OperationRegister {
    public ArrayList<BoardingTotal> getBoardingTotals(String timeDomain) {
        StationPersistanceHandler sph = new StationPersistanceHandler();
        if (timeDomain.equals("Daily")) {
            return sph.getBoardingTotalsByDay();
        } else if (timeDomain.equals("Monthly")) {
            return sph.getBoardingTotalsByMonth();
        } else if (timeDomain.equals("Yearly")) {
            return sph.getBoardingTotalsByYear();
        }
        return null;
    }
    
    public ArrayList<Bus> getFreeBuses() {
        BusPersistanceHandler bph = new BusPersistanceHandler();
        return bph.getFreeBuses();
    }

    public void allocateBusToRoute(int busId, int routeId) {
        BusPersistanceHandler bph = new BusPersistanceHandler();
        Bus bus = (Bus) bph.find(busId);
        if (bus == null) {
            System.out.println("Invalid Bus ID");
            return;
        }
        bus.setRouteID(routeId);
        bph.save(bus);
    }

    public ArrayList<Bus> getBusesByRoute(int routeId) {
        BusPersistanceHandler bph = new BusPersistanceHandler();
        return bph.getBusesByRoute(routeId);
    }

}
