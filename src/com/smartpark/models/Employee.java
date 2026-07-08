package com.smartpark.models;

public abstract class Employee extends Person {
    protected String employeeType;
    protected String shift;
    protected double salary;

    public Employee(int userId, String name, String contactNumber, String employeeType, String shift, double salary) {
        super(userId, name, contactNumber);
        this.employeeType = employeeType;
        this.shift = shift;
        this.salary = salary;
    }

    public String getEmployeeType() { return employeeType; }
    public void setEmployeeType(String employeeType) { this.employeeType = employeeType; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}