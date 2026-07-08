package com.smartpark.models;

public class Customer extends Person {
    private String email;

    public Customer(int userId, String name, String contactNumber, String email) {
        super(userId, name, contactNumber);
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}