package com.smartpark.models;
import java.math.BigDecimal;

public class ParkingOperator extends Employee {
    public ParkingOperator(int userId, String name, String contactNumber, String shift, BigDecimal salary) {
        super(userId, name, contactNumber, "Parking Operator", shift, salary);
    }
}