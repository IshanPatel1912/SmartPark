package com.smartpark.models;

public class ParkingSlot {
    private int id;
    private int floorId;
    private String slotNumber;
    private String slotType;
    private String status;

    public ParkingSlot(int id, int floorId, String slotNumber, String slotType, String status) {
        this.id = id;
        this.floorId = floorId;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFloorId() { return floorId; }
    public void setFloorId(int floorId) { this.floorId = floorId; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public String getSlotType() { return slotType; }
    public void setSlotType(String slotType) { this.slotType = slotType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}