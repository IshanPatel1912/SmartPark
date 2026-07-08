package com.smartpark.models;

public class Admin extends Employee {
    public Admin(int userId, String name, String contactNumber, String shift, double salary) {
        super(userId, name, contactNumber, "Admin", shift, salary);
    }
}