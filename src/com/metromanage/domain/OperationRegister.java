package com.metromanage.domain;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.metromanage.model.StationPersistanceHandler;

public class OperationRegister {
    public ArrayList<BoardingTotal> getBoardingTotals(String timeDomain){
        StationPersistanceHandler sph = new StationPersistanceHandler();
        if(timeDomain.equals("Daily")){
            return sph.getBoardingTotalsByDay();
        } else if (timeDomain.equals("Monthly")){
            return sph.getBoardingTotalsByMonth();
        } else if (timeDomain.equals("Yearly")){
            return sph.getBoardingTotalsByYear();
        }
        return null;
    }
}
