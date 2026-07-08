package com.smartpark.models;

public class ParkingOperator extends Employee {
    public ParkingOperator(int userId, String name, String contactNumber, String shift, double salary) {
        super(userId, name, contactNumber, "Parking Operator", shift, salary);
    }
}