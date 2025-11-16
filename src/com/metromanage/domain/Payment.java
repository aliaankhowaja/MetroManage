package com.metromanage.domain;
import java.time.LocalDateTime;
public interface Payment {
    
    public int getPaymentID();

    public int getPassengerID();

    public float getAmount();

    public String getPaymentDate();
    

}