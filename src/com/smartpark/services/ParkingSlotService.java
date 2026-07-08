package com.smartpark.services;

import com.smartpark.dao.ParkingSlotDAO;
import com.smartpark.models.ParkingSlot;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ParkingSlotService {

    private ParkingSlotDAO parkingSlotDAO;
    private HashMap<Integer, ParkingSlot> slotCache;

    public ParkingSlotService() {
        this.parkingSlotDAO = new ParkingSlotDAO();
        this.slotCache = new HashMap<>();
    }

    public void generateSlotsForFloor(int floorId, int floorNumber, int bikes, int cars, int suvs, int evs) throws SQLException {
        int slotCounter = 1;
        createSlots(floorId, floorNumber, "Bike", bikes, slotCounter);
        slotCounter += bikes;
        createSlots(floorId, floorNumber, "Car", cars, slotCounter);
        slotCounter += cars;
        createSlots(floorId, floorNumber, "SUV", suvs, slotCounter);
        slotCounter += suvs;
        createSlots(floorId, floorNumber, "EV", evs, slotCounter);
    }

    private void createSlots(int floorId, int floorNumber, String type, int count, int startCounter) throws SQLException {
        for (int i = 0; i < count; i++) {
            String slotNumber = "F" + floorNumber + "-" + type.charAt(0) + (startCounter + i);
            parkingSlotDAO.addSlot(new ParkingSlot(0, floorId, slotNumber, type, "AVAILABLE"));
        }
    }

    public ParkingSlot getNearestAvailableSlot(String slotType) throws SQLException {
        return parkingSlotDAO.getNearestAvailableSlot(slotType);
    }

    public List<ParkingSlot> getAvailableSlots(String slotType) throws SQLException {
        return parkingSlotDAO.getAvailableSlots(slotType);
    }

    public ParkingSlot getSlotDetails(int slotId) throws SQLException {
        if (slotCache.containsKey(slotId)) return slotCache.get(slotId);
        ParkingSlot slot = parkingSlotDAO.getSlotById(slotId);
        if (slot != null) slotCache.put(slotId, slot);
        return slot;
    }

    public void updateSlotStatus(int slotId, String status) throws SQLException {
        parkingSlotDAO.updateSlotStatus(slotId, status);
        if (slotCache.containsKey(slotId)) {
            slotCache.get(slotId).setStatus(status);
        }
    }

    public void addExtraSlotsToFloor(int floorId, int floorNumber, String type, int numberOfNewSlots) throws SQLException {
       
        int currentMax = 0;
        List<ParkingSlot> allSlots = parkingSlotDAO.getAvailableSlots(type); 
        for (ParkingSlot slot : allSlots) {
            if (slot.getFloorId() == floorId && slot.getSlotNumber().startsWith("F" + floorNumber + "-" + type.charAt(0))) {
                
                String numPart = slot.getSlotNumber().substring(slot.getSlotNumber().indexOf(type.charAt(0)) + 1);
                try {
                    int num = Integer.parseInt(numPart);
                    if (num > currentMax) currentMax = num;
                } catch (Exception ignored) {}
            }
        }
        
        
        createSlots(floorId, floorNumber, type, numberOfNewSlots, currentMax + 1);
        System.out.println("Successfully added " + numberOfNewSlots + " " + type + " slots to Floor " + floorNumber + ".");
    }

}