package com.smartpark.models;

public class Manager extends Employee {
    public Manager(int userId, String name, String contactNumber, String shift, double salary) {
        super(userId, name, contactNumber, "Manager", shift, salary);
    }
}