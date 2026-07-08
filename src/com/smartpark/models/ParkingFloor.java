package com.smartpark.models;

public class ParkingFloor {
    private int id;
    private int floorNumber;
    private int totalCapacity;

    public ParkingFloor(int id, int floorNumber, int totalCapacity) {
        this.id = id;
        this.floorNumber = floorNumber;
        this.totalCapacity = totalCapacity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }
}