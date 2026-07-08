package com.smartpark.models;

public class SecurityGuard extends Employee {
    public SecurityGuard(int userId, String name, String contactNumber, String shift, double salary) {
        super(userId, name, contactNumber, "Security Guard", shift, salary);
    }
}