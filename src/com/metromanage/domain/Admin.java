package com.metromanage.domain;

import java.time.LocalDateTime;

import com.metromanage.model.AdminPersistanceHandler;

public class Admin {
    private int adminID;
    private String name;
    private String email;
    private String passwordHash;
    private String status;
    private LocalDateTime registrationDate;

    public Admin() {}

    public Admin(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = "Active";
        this.registrationDate = LocalDateTime.now();
        AdminPersistanceHandler aph = new AdminPersistanceHandler();
        this.adminID = aph.save(this);
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
