package com.smartpark.models;
import java.math.BigDecimal;

public class SecurityGuard extends Employee {
    public SecurityGuard(int userId, String name, String contactNumber, String shift, BigDecimal salary) {
        super(userId, name, contactNumber, "Security Guard", shift, salary);
    }
}