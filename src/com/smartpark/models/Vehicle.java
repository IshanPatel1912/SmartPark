package com.smartpark.models;

public class Vehicle {
    private int id;
    private int customerId;
    private String licensePlate;
    private String vehicleType;

    public Vehicle(int id, int customerId, String licensePlate, String vehicleType) {
        this.id = id;
        this.customerId = customerId;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}