package com.petcare.model;

import java.time.LocalDate;

/**
 * Customer entity representing Pet Owners.
 */
public class Customer {
    private int id;
    private Integer userId; // Optional link to User account
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;

    public Customer() {}

    public Customer(int id, Integer userId, String fullName, String email, String phone, String address, LocalDate registrationDate) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    public Customer(String fullName, String email, String phone, String address, LocalDate registrationDate) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return fullName + " (ID: " + id + ")";
    }
}
