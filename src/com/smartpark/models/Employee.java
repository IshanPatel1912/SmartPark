package com.smartpark.models;
import java.math.BigDecimal;

public abstract class Employee extends Person {
    private String employeeType;
    private String shift;
    private BigDecimal salary;

    public Employee(int userId, String name, String contactNumber, String employeeType, String shift, BigDecimal salary) {
        super(userId, name, contactNumber);
        this.employeeType = employeeType;
        this.shift = shift;
        this.salary = salary;
    }

    public String getEmployeeType() { return employeeType; }
    public void setEmployeeType(String employeeType) { this.employeeType = employeeType; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
}