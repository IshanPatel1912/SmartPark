package com.smartpark.models;
import java.math.BigDecimal;

public class Manager extends Employee {
    public Manager(int userId, String name, String contactNumber, String shift, BigDecimal salary) {
        super(userId, name, contactNumber, "Manager", shift, salary);
    }
}