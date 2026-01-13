# MetroManage – Sequence Diagrams (UC01–UC13)

---

## UC01 – Manage Users

```mermaid
sequenceDiagram
    participant AdminRegister
    participant Passenger
    participant PassengerPersistanceHandler

    note over AdminRegister: Admin chooses an action: add / update / delete / search

    alt Add passenger
        AdminRegister->>PassengerPersistanceHandler: findByEmail(email)
        activate PassengerPersistanceHandler
        PassengerPersistanceHandler-->>AdminRegister: existingPassenger (null if not found)
        deactivate PassengerPersistanceHandler
        AdminRegister->>Passenger: new Passenger(name, email, phone, passwordHash, walletBalance)
        activate Passenger
        Passenger->>PassengerPersistanceHandler: save(this)
        activate PassengerPersistanceHandler
        PassengerPersistanceHandler-->>Passenger: passengerID
        deactivate PassengerPersistanceHandler
        Passenger-->>AdminRegister: passenger
        deactivate Passenger
    else Update passenger
        AdminRegister->>PassengerPersistanceHandler: findByEmail(email)
        activate PassengerPersistanceHandler
        PassengerPersistanceHandler-->>AdminRegister: passenger
        deactivate PassengerPersistanceHandler
        AdminRegister->>Passenger: setName(), setPhoneNumber(), setPasswordHash(), setStatus(), setWalletBalance()
        activate Passenger
        Passenger-->>AdminRegister: updatedPassenger
        deactivate Passenger
        AdminRegister->>PassengerPersistanceHandler: save(passenger)
        activate PassengerPersistanceHandler
        PassengerPersistanceHandler-->>AdminRegister: passengerID
        deactivate PassengerPersistanceHandler
    else Delete passenger
        AdminRegister->>PassengerPersistanceHandler: findByEmail(email)
        activate PassengerPersistanceHandler
        PassengerPersistanceHandler-->>AdminRegister: passenger
        deactivate PassengerPersistanceHandler
        AdminRegister->>Passenger: markDeleted()
        activate Passenger
        Passenger->>PassengerPersistanceHandler: delete(this)
        activate PassengerPersistanceHandler
        Note over PassengerPersistanceHandler: Sets status to "deleted" and saves
        PassengerPersistanceHandler-->>Passenger: ok
        deactivate PassengerPersistanceHandler
        deactivate Passenger
    else Search passengers
        AdminRegister->>PassengerPersistanceHandler: searchPassengers(searchTerm, includeDeleted)
        activate PassengerPersistanceHandler
        PassengerPersistanceHandler-->>AdminRegister: passengerList
        deactivate PassengerPersistanceHandler
    end
```

---

## UC02 – Manage Fleet

```mermaid
sequenceDiagram
    participant AdminRegister
    participant Bus
    participant BusPersistanceHandler

    note over AdminRegister: Admin chooses an action: add / update / delete / search

    alt Add bus
        AdminRegister->>Bus: new Bus(licensePlate, capacity, status, routeID)
        activate Bus
        Bus->>BusPersistanceHandler: save(this)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>Bus: busID
        deactivate BusPersistanceHandler
        Bus-->>AdminRegister: bus
        deactivate Bus
    else Update bus
        AdminRegister->>BusPersistanceHandler: find(busID)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>AdminRegister: bus
        deactivate BusPersistanceHandler
        AdminRegister->>Bus: setPlateNumber(), setCapacity(), setStatus(), setRouteID()
        activate Bus
        Bus-->>AdminRegister: updatedBus
        deactivate Bus
        AdminRegister->>BusPersistanceHandler: save(bus)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>AdminRegister: busID
        deactivate BusPersistanceHandler
    else Delete bus
        AdminRegister->>BusPersistanceHandler: find(busID)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>AdminRegister: bus
        deactivate BusPersistanceHandler
        AdminRegister->>Bus: markDeleted()
        activate Bus
        Bus->>BusPersistanceHandler: delete(this)
        activate BusPersistanceHandler
        Note over BusPersistanceHandler: Sets status to "Deleted" and saves
        BusPersistanceHandler-->>Bus: ok
        deactivate BusPersistanceHandler
        deactivate Bus
    else Search buses
        AdminRegister->>BusPersistanceHandler: searchBuses(searchTerm, includeDeleted)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>AdminRegister: busList
        deactivate BusPersistanceHandler
    end
```

---

## UC03 – Allocate Buses

```mermaid
sequenceDiagram
    participant OperationRegister
    participant BusPersistanceHandler
    participant Bus

    OperationRegister->>BusPersistanceHandler: getFreeBuses()
    activate BusPersistanceHandler
    BusPersistanceHandler-->>OperationRegister: freeBusesList
    deactivate BusPersistanceHandler
    
    loop for each allocation
        OperationRegister->>BusPersistanceHandler: find(busId)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>OperationRegister: bus
        deactivate BusPersistanceHandler
        OperationRegister->>Bus: setRouteID(routeId)
        activate Bus
        Bus-->>OperationRegister: ok
        deactivate Bus
        OperationRegister->>BusPersistanceHandler: save(bus)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>OperationRegister: busID
        deactivate BusPersistanceHandler
    end
```

---

## UC04 – Re-allocate Buses based on Delay Index

```mermaid
sequenceDiagram
    participant OperationRegister
    participant BusPersistanceHandler
    participant Bus

    OperationRegister->>BusPersistanceHandler: getBusesByRoute(routeId)
    activate BusPersistanceHandler
    BusPersistanceHandler-->>OperationRegister: busesOnRoute
    deactivate BusPersistanceHandler
    
    OperationRegister->>BusPersistanceHandler: getFreeBuses()
    activate BusPersistanceHandler
    BusPersistanceHandler-->>OperationRegister: availableBuses
    deactivate BusPersistanceHandler

    alt Admin decides to reallocate
        OperationRegister->>BusPersistanceHandler: find(busId)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>OperationRegister: bus
        deactivate BusPersistanceHandler
        OperationRegister->>Bus: setRouteID(newRouteId)
        activate Bus
        Bus-->>OperationRegister: ok
        deactivate Bus
        OperationRegister->>BusPersistanceHandler: save(bus)
        activate BusPersistanceHandler
        BusPersistanceHandler-->>OperationRegister: busID
        deactivate BusPersistanceHandler
    end
```

---

## UC05 – View Boarding Totals

```mermaid
sequenceDiagram
    participant OperationRegister
    participant StationPersistanceHandler
    participant BoardingTotal

    OperationRegister->>OperationRegister: getBoardingTotals(timeDomain)
    Note over OperationRegister: timeDomain: "Daily" / "Monthly" / "Yearly"
    
    alt timeDomain == "Daily"
        OperationRegister->>StationPersistanceHandler: getBoardingTotalsByDay()
        activate StationPersistanceHandler
    else timeDomain == "Monthly"
        OperationRegister->>StationPersistanceHandler: getBoardingTotalsByMonth()
        activate StationPersistanceHandler
    else timeDomain == "Yearly"
        OperationRegister->>StationPersistanceHandler: getBoardingTotalsByYear()
        activate StationPersistanceHandler
    end
    
    Note over StationPersistanceHandler: Executes SQL JOIN on Ride, Route, Station tables
    loop for each result row
        StationPersistanceHandler->>BoardingTotal: new BoardingTotal(stationName, routeName, totalBoardings, date, month, year)
        activate BoardingTotal
        BoardingTotal-->>StationPersistanceHandler: boardingTotal
        deactivate BoardingTotal
    end
    StationPersistanceHandler-->>OperationRegister: boardingTotalsList
    deactivate StationPersistanceHandler
```

---

## UC06 – Passenger Login

```mermaid
sequenceDiagram
    participant LoginHandler
    participant SessionManager
    participant PassengerPersistanceHandler
    participant Passenger

    LoginHandler->>SessionManager: isLoggedIn()
    activate SessionManager
    SessionManager-->>LoginHandler: loggedInStatus
    deactivate SessionManager
    
    alt already logged in
        LoginHandler-->>LoginHandler: return 1 (already logged in)
    else not logged in
        LoginHandler->>PassengerPersistanceHandler: findByEmail(email)
        activate PassengerPersistanceHandler
        PassengerPersistanceHandler-->>LoginHandler: passenger (or null)
        deactivate PassengerPersistanceHandler
        
        alt passenger not found
            LoginHandler-->>LoginHandler: return 2 (invalid email)
        else passenger found
            LoginHandler->>Passenger: GenerateHash(password)
            activate Passenger
            Passenger-->>LoginHandler: hashedPassword
            deactivate Passenger
            LoginHandler->>Passenger: getPasswordHash()
            activate Passenger
            Passenger-->>LoginHandler: storedHash
            deactivate Passenger
            
            alt passwords match
                LoginHandler->>SessionManager: createSession(passenger)
                activate SessionManager
                SessionManager-->>LoginHandler: ok
                deactivate SessionManager
                LoginHandler-->>LoginHandler: return 0 (success)
            else passwords don't match
                LoginHandler-->>LoginHandler: return 3 (invalid password)
            end
        end
    end
```

---

## UC07 – Purchase Ticket

```mermaid
sequenceDiagram
    participant StationRegister
    participant Route
    participant Ticket
    participant Payment
    participant Ride
    participant TicketPersistanceHandler
    participant PaymentPersistanceHandler
    participant RidePersistanceHandler
    participant PassengerPersistanceHandler

    StationRegister->>Route: getCost()
    activate Route
    Route-->>StationRegister: cost
    deactivate Route
    
    StationRegister->>Ticket: new Ticket(passenger, cost, paymentMethod, paymentDetails)
    activate Ticket
    
    alt paymentMethod is "Wallet"
        Ticket->>Ticket: Check wallet balance >= cost
        Ticket->>Payment: new WalletPayment(passengerID, cost, date)
        activate Payment
        Payment->>PaymentPersistanceHandler: savePayment(this, "Wallet")
        activate PaymentPersistanceHandler
        PaymentPersistanceHandler-->>Payment: paymentID
        deactivate PaymentPersistanceHandler
        Payment-->>Ticket: paymentID
        deactivate Payment
    else paymentMethod is "Card"
        Ticket->>Payment: new CardPayment(passengerID, cost, date, cardNumber, holderName, expiry)
        activate Payment
        Payment->>PaymentPersistanceHandler: savePayment(this, "Card")
        activate PaymentPersistanceHandler
        PaymentPersistanceHandler-->>Payment: paymentID
        deactivate PaymentPersistanceHandler
        Payment-->>Ticket: paymentID
        deactivate Payment
    else paymentMethod is "Cash"
        Ticket->>Payment: new CashPayment(passengerID, cost, date)
        activate Payment
        Payment->>PaymentPersistanceHandler: savePayment(this, "Cash")
        activate PaymentPersistanceHandler
        PaymentPersistanceHandler-->>Payment: paymentID
        deactivate PaymentPersistanceHandler
        Payment-->>Ticket: paymentID
        deactivate Payment
    end
    
    Ticket->>TicketPersistanceHandler: save(this)
    activate TicketPersistanceHandler
    TicketPersistanceHandler-->>Ticket: ticketID
    deactivate TicketPersistanceHandler
    
    Ticket->>PassengerPersistanceHandler: save(passenger)
    activate PassengerPersistanceHandler
    Note over PassengerPersistanceHandler: Updates wallet balance if Wallet payment
    PassengerPersistanceHandler-->>Ticket: ok
    deactivate PassengerPersistanceHandler
    
    Ticket-->>StationRegister: ticket
    deactivate Ticket
    
    StationRegister->>Ride: new Ride(route, ticket, boardingStationID, 0)
    activate Ride
    Ride->>RidePersistanceHandler: save(this)
    activate RidePersistanceHandler
    RidePersistanceHandler-->>Ride: rideID
    deactivate RidePersistanceHandler
    Ride-->>StationRegister: ride
    deactivate Ride
    
    StationRegister->>Ticket: setRide(ride)
```

---

## UC08 – Checkin

```mermaid
sequenceDiagram
    participant StationRegister
    participant TicketPersistanceHandler
    participant Ticket
    participant RidePersistanceHandler
    participant Ride
    participant BusPersistanceHandler
    participant Bus

    StationRegister->>TicketPersistanceHandler: find(ticketId)
    activate TicketPersistanceHandler
    TicketPersistanceHandler-->>StationRegister: ticket
    deactivate TicketPersistanceHandler
    
    alt ticket is null
        StationRegister-->>StationRegister: "Invalid Ticket ID"
    else ticket found
        StationRegister->>Ticket: isValidTicket()
        activate Ticket
        Note over Ticket: Checks status="Active" AND now < expiryTime
        Ticket-->>StationRegister: valid/invalid
        deactivate Ticket
        
        alt ticket invalid or expired
            StationRegister-->>StationRegister: "Ticket invalid or expired"
        else ticket valid
            StationRegister->>RidePersistanceHandler: find(ticketId)
            activate RidePersistanceHandler
            RidePersistanceHandler-->>StationRegister: ride
            deactivate RidePersistanceHandler
            
            StationRegister->>BusPersistanceHandler: find(busId)
            activate BusPersistanceHandler
            BusPersistanceHandler-->>StationRegister: bus
            deactivate BusPersistanceHandler
            
            alt ride.route.routeID != bus.routeID
                StationRegister-->>StationRegister: "Bus does not serve route"
            else bus.status == "Inactive"
                StationRegister-->>StationRegister: "Bus is inactive"
            else valid check-in
                StationRegister->>Ticket: setStatus("Used")
                StationRegister->>Ride: setBoardingTime(now)
                StationRegister->>Ride: setBus(bus)
                StationRegister->>Ride: setBoardingStationID(stationID)
                StationRegister->>RidePersistanceHandler: save(ride)
                activate RidePersistanceHandler
                RidePersistanceHandler-->>StationRegister: ok
                deactivate RidePersistanceHandler
                StationRegister->>TicketPersistanceHandler: save(ticket)
                activate TicketPersistanceHandler
                TicketPersistanceHandler-->>StationRegister: ok
                deactivate TicketPersistanceHandler
                StationRegister-->>StationRegister: "Check-in successful"
            end
        end
    end
```

---

## UC09 – Submit Feedback

```mermaid
sequenceDiagram
    participant StationRegister
    participant Feedback
    participant FeedbackPersistanceHandler

    StationRegister->>StationRegister: submitFeedback(passengerID, type, comments)
    StationRegister->>Feedback: new Feedback(passengerID, type, comments)
    activate Feedback
    Note over Feedback: Sets timestamp = LocalDateTime.now()
    Feedback->>FeedbackPersistanceHandler: save(this)
    activate FeedbackPersistanceHandler
    FeedbackPersistanceHandler-->>Feedback: feedbackID
    deactivate FeedbackPersistanceHandler
    Feedback-->>StationRegister: feedback
    deactivate Feedback
    StationRegister-->>StationRegister: "Feedback submitted with ID: feedbackID"
```

---

## UC10 – CheckOut

```mermaid
sequenceDiagram
    participant StationRegister
    participant TicketPersistanceHandler
    participant Ticket
    participant RidePersistanceHandler
    participant Ride

    StationRegister->>TicketPersistanceHandler: find(ticketId)
    activate TicketPersistanceHandler
    TicketPersistanceHandler-->>StationRegister: ticket
    deactivate TicketPersistanceHandler
    
    alt ticket is null
        StationRegister-->>StationRegister: "Invalid Ticket ID"
    else ticket found
        StationRegister->>RidePersistanceHandler: find(ticketId)
        activate RidePersistanceHandler
        RidePersistanceHandler-->>StationRegister: ride
        deactivate RidePersistanceHandler
        
        StationRegister->>Ticket: setRide(ride)
        StationRegister->>Ticket: isValidForCheckout()
        activate Ticket
        Note over Ticket: Checks status="Used" AND now < expiryTime AND ride.isActive=true
        Ticket-->>StationRegister: valid/invalid
        deactivate Ticket
        
        alt not valid for checkout
            StationRegister-->>StationRegister: "Ticket not valid for checkout"
        else valid for checkout
            StationRegister->>Ride: setArrivalTime(now)
            StationRegister->>Ride: setArrivalStationID(arrivalStationID)
            StationRegister->>Ride: setActive(false)
            StationRegister->>RidePersistanceHandler: save(ride)
            activate RidePersistanceHandler
            RidePersistanceHandler-->>StationRegister: ok
            deactivate RidePersistanceHandler
            StationRegister-->>StationRegister: "Check-out successful"
        end
    end
```

---

## UC11 – View Schedule

```mermaid
sequenceDiagram
    participant OperationRegister
    participant RoutePersistanceHandler
    participant Route
    participant BusPersistanceHandler

    OperationRegister->>OperationRegister: getSchedule(routeID)
    OperationRegister->>RoutePersistanceHandler: find(routeID)
    activate RoutePersistanceHandler
    RoutePersistanceHandler-->>OperationRegister: route
    deactivate RoutePersistanceHandler
    
    OperationRegister->>BusPersistanceHandler: getActiveBusesForRoute(routeID)
    activate BusPersistanceHandler
    BusPersistanceHandler-->>OperationRegister: activeBusesList
    deactivate BusPersistanceHandler
    
    alt no buses or route not found
        OperationRegister-->>OperationRegister: return -1 (no schedule available)
    else calculate schedule
        OperationRegister->>Route: getEstimatedTime()
        activate Route
        Route-->>OperationRegister: estimatedTime
        deactivate Route
        Note over OperationRegister: interval = estimatedTime / buses.size()
        OperationRegister-->>OperationRegister: return interval (minutes until next bus)
    end
```

---

## UC12 – Check Balance

```mermaid
sequenceDiagram
    participant StationRegister
    participant PassengerPersistanceHandler
    participant Passenger

    StationRegister->>StationRegister: checkBalance(passengerID)
    StationRegister->>PassengerPersistanceHandler: find(passengerID)
    activate PassengerPersistanceHandler
    PassengerPersistanceHandler-->>StationRegister: passenger
    deactivate PassengerPersistanceHandler
    
    alt passenger not found
        StationRegister-->>StationRegister: return -1
    else passenger found
        StationRegister->>Passenger: getWalletBalance()
        activate Passenger
        Passenger-->>StationRegister: walletBalance
        deactivate Passenger
        StationRegister-->>StationRegister: return walletBalance
    end
```

---

## UC13 – View Peak Hours

```mermaid
sequenceDiagram
    participant OperationRegister
    participant StationPersistanceHandler
    participant BoardingTotal

    OperationRegister->>OperationRegister: getPeakHours(stationID)
    OperationRegister->>StationPersistanceHandler: getBoardingTotalsByHour(stationID)
    activate StationPersistanceHandler
    Note over StationPersistanceHandler: SQL query groups by HOUR(boardingTime)
    loop for each hour with data
        StationPersistanceHandler->>BoardingTotal: new BoardingTotal(stationName, routeName, totalBoardings, hour, 0, 0)
        activate BoardingTotal
        BoardingTotal-->>StationPersistanceHandler: boardingTotal
        deactivate BoardingTotal
    end
    StationPersistanceHandler-->>OperationRegister: boardingTotalsList (ordered by hour ASC)
    deactivate StationPersistanceHandler
```
