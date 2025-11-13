package com.metromanage.domain;

import java.time.LocalDateTime;

public class Ticket{
    private String ticketID;
    private Ride ride;
    private LocalDateTime issueTime;
    private String purchaseDate;
    private String status;
}