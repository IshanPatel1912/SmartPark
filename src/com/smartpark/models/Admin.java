package com.smartpark.models;
import java.math.BigDecimal;

public class Admin extends Employee {
    public Admin(int userId, String name, String contactNumber, String shift, BigDecimal salary) {
        super(userId, name, contactNumber, "Admin", shift, salary);
    }
}