package com.smartpark.models;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int customerId;
    private int vehicleId;
    private int slotId;
    private LocalDateTime reservationTime;
    private LocalDateTime expiryTime;
    private String status;

    public Reservation(int id, int customerId, int vehicleId, int slotId, LocalDateTime reservationTime, LocalDateTime expiryTime, String status) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.slotId = slotId;
        this.reservationTime = reservationTime;
        this.expiryTime = expiryTime;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public LocalDateTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalDateTime reservationTime) { this.reservationTime = reservationTime; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}