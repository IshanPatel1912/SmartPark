package com.smartpark.services;

import com.smartpark.dao.ParkingSessionDAO;
import com.smartpark.dao.ReservationDAO;
import com.smartpark.exceptions.SlotOccupiedException;
import com.smartpark.models.Bill;
import com.smartpark.models.ParkingSession;
import com.smartpark.models.ParkingSlot;
import com.smartpark.models.Reservation;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ParkingSessionService {

    private ParkingSessionDAO sessionDAO;
    private ParkingSlotService slotService;
    private BillService billService;
    private ReservationDAO reservationDAO;

    private Queue<Integer> entryQueue;
    private Queue<Integer> exitQueue;
    private Stack<ParkingSession> recentSessions;

    public ParkingSessionService(ParkingSlotService slotService, BillService billService) {
        this.sessionDAO = new ParkingSessionDAO();
        this.slotService = slotService;
        this.billService = billService;
        this.reservationDAO = new ReservationDAO();
        this.entryQueue = new LinkedList<>();
        this.exitQueue = new LinkedList<>();
        this.recentSessions = new Stack<>();
    }

    public void addVehicleToEntryQueue(int vehicleId) {
        entryQueue.offer(vehicleId);
    }

    public void addVehicleToExitQueue(int vehicleId) {
        exitQueue.offer(vehicleId);
    }

    public ParkingSession processNextEntry(int reservationId) throws SQLException, SlotOccupiedException {
        Integer vehicleId = entryQueue.poll();
        if (vehicleId == null) return null;

        int assignedSlotId;

        if (reservationId > 0) {
            Reservation res = reservationDAO.getReservationById(reservationId);
            
            if (res == null || !"ACTIVE".equalsIgnoreCase(res.getStatus())) {
                throw new SlotOccupiedException("Invalid or Expired Reservation ID.");
            }
            if (res.getVehicleId() != vehicleId) {
                throw new SlotOccupiedException("Vehicle mismatch! This reservation belongs to another vehicle.");
            }
            
            assignedSlotId = res.getSlotId();
            reservationDAO.updateReservationStatus(reservationId, "USED");

        } else {
            String vehicleType = getVehicleTypeById(vehicleId);
            ParkingSlot nearestSlot = slotService.getNearestAvailableSlot(vehicleType);

            if (nearestSlot == null) {
                throw new SlotOccupiedException("Parking is FULL for vehicle type: " + vehicleType);
            }
            assignedSlotId = nearestSlot.getId();
        }

        ParkingSession session = new ParkingSession(0, reservationId, vehicleId, assignedSlotId, LocalDateTime.now(), null, "ACTIVE");
        int sessionId = sessionDAO.createSession(session);

        if (sessionId > 0) {
            slotService.updateSlotStatus(assignedSlotId, "OCCUPIED");
            session.setId(sessionId);
            return session;
        }
        return null;
    }

    public Bill processNextExit() throws SQLException {
        Integer vehicleId = exitQueue.poll();
        if (vehicleId == null) return null;

        ParkingSession session = sessionDAO.getActiveSessionByVehicleId(vehicleId);
        if (session == null) return null;

        LocalDateTime exitTime = LocalDateTime.now();
        sessionDAO.updateExitDetails(session.getId(), exitTime, "COMPLETED");
        slotService.updateSlotStatus(session.getSlotId(), "AVAILABLE");
        
        session.setExitTime(exitTime);
        session.setStatus("COMPLETED");
        recentSessions.push(session);

        long hoursParked = ChronoUnit.HOURS.between(session.getEntryTime(), exitTime);
        if (hoursParked == 0) hoursParked = 1;

        String vehicleType = getVehicleTypeById(vehicleId);
        return billService.generateBill(session.getId(), vehicleType, hoursParked);
    }

    public ParkingSession undoLastExit() throws SQLException {
        if (recentSessions.isEmpty()) return null;
        
        ParkingSession lastSession = recentSessions.pop();
        sessionDAO.updateExitDetails(lastSession.getId(), null, "ACTIVE");
        slotService.updateSlotStatus(lastSession.getSlotId(), "OCCUPIED");
        
        return lastSession;
    }

    private String getVehicleTypeById(int vehicleId) throws SQLException {
        String query = "SELECT vehicle_type FROM vehicles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("vehicle_type");
            }
        }
        return "Car";
    }
}