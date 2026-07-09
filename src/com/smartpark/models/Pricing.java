package com.smartpark.models;
import java.math.BigDecimal;

public class Pricing {
    private int id;
    private String vehicleType;
    private BigDecimal baseRate;
    private BigDecimal hourlyRate;

    public Pricing(int id, String vehicleType, BigDecimal baseRate, BigDecimal hourlyRate) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.baseRate = baseRate;
        this.hourlyRate = hourlyRate;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
}