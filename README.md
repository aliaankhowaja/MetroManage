# MetroManage

MetroManage is a comprehensive Metro Management System application built with Java and Swing. It streamlines the operations of a metro service, offering features for passengers, administrators, and operation managers to handle ticketing, route management, fleet operations, and more.

## Features

### For Passengers
*   **User Account:** Sign up and login functionality.
*   **Ticket Purchase:** Easy interface to buy tickets.
*   **Wallet:** Check and manage wallet balance.
*   **Schedules:** View metro schedules.
*   **Feedback:** Submit feedback to the administration.

### For Administrators & Operations
*   **Fleet Management:** Add, update, and manage buses.
*   **Bus Allocation:** Allocate buses to specific routes.
*   **Route & Station Management:** Manage connectivity and stations.
*   **Analytics:** View boarding totals, peak hours, and passenger feedback.
*   **User Management:** Oversee system users.

## Technology Stack

*   **Programming Language:** Java
*   **GUI Framework:** Java Swing
*   **Database:** Microsoft SQL Server (MSSQL)
*   **Persistence:** JDBC (Java Database Connectivity)

## Prerequisites

*   Java Development Kit (JDK) 8 or higher.
*   Microsoft SQL Server using Windows Authentication.
*   MSSQL JDBC Driver (included in `lib/` or requires setup).

## Database Setup

1.  **Database Creation:** Ensure a database named `MetroManage` exists in your SQL Server instance.
2.  **Connection Configuration:**
    The application connects to the database using the following settings (found in `src/com/metromanage/model/DB.java`):
    *   **Host:** `localhost`
    *   **Port:** `55510`
    *   **Database Name:** `MetroManage`
    *   **Authentication:** Integrated Security (Windows Authentication)
    
    *Note: You may need to update the port or connection string in `DB.java` to match your local SQL Server configuration.*

3.  **Schema Initialization:**
    The database schema is described in `tables.txt`. The application supports creating tables programmatically via `DB.createTables()`. You may need to uncomment this line in `Main.java` for the first run or execute the SQL commands manually.

## Directory Structure

*   `src/`: Contains the Java source code organized by packages (`com.metromanage`).
    *   `domain/`: Domain entities and logic (Bus, Passenger, Ticket, etc.).
    *   `model/`: Database persistence handlers (DAO pattern).
    *   `ui/`: Swing GUI form classes.
*   `bin/Resources/`: Application resources like images (`background2.jpg`, `logo.png`).
*   `lib/`: Contains external libraries (e.g., MSSQL JDBC driver).
*   `tables.txt`: Simple text representation of the database schema.

## How to Run

1.  Compile the project, ensuring the JDK and the JDBC driver library are in your classpath.
2.  Run the main entry point: `com.metromanage.Main`.
3.  The application will launch the **Welcome Page**.

## Usage

*   **Main Entry:** `Main.java` initializes the application.
*   **Login:** Users can log in as Passenger, Admin, or Operation.
*   **Navigation:** The dashboard provides access to role-specific features.
