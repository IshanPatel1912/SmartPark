package com.smartpark.models;

public class Pricing {
    private int id;
    private String vehicleType;
    private double baseRate;
    private double hourlyRate;

    public Pricing(int id, String vehicleType, double baseRate, double hourlyRate) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.baseRate = baseRate;
        this.hourlyRate = hourlyRate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public double getBaseRate() { return baseRate; }
    public void setBaseRate(double baseRate) { this.baseRate = baseRate; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
}