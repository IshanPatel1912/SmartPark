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
import java.time.Duration;
import java.time.LocalDateTime;
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
        if (!entryQueue.contains(vehicleId)) {
            entryQueue.offer(vehicleId);
        } else {
            System.out.println("Notice: Vehicle is already in the entry queue.");
        }
    }

    public void addVehicleToExitQueue(int vehicleId) {
        if (!exitQueue.contains(vehicleId)) {
            exitQueue.offer(vehicleId);
        } else {
            System.out.println("Notice: Vehicle is already in the exit queue.");
        }
    }

    public ParkingSession processNextEntry(int reservationId) throws SQLException, SlotOccupiedException {
        Integer vehicleId = entryQueue.poll();
        if (vehicleId == null) return null;

        try {
            DBConnection.beginTransaction(); 
            
            if (sessionDAO.getActiveSessionByVehicleId(vehicleId) != null) {
                throw new SlotOccupiedException("Vehicle is already parked inside the system!");
            }

            int assignedSlotId;

            if (reservationId > 0) {
                Reservation res = reservationDAO.getReservationById(reservationId);
                if (res == null || !"ACTIVE".equalsIgnoreCase(res.getStatus())) {
                    throw new SlotOccupiedException("Invalid or previously used Reservation ID.");
                }
                if (LocalDateTime.now().isAfter(res.getExpiryTime())) {
                    reservationDAO.updateReservationStatus(reservationId, "EXPIRED");
                    
                    // BUG FIX #1: Free up the slot forever locked by expired reservations
                    slotService.updateSlotStatus(res.getSlotId(), "AVAILABLE");
                    DBConnection.commitTransaction(); 
                    throw new SlotOccupiedException("This reservation has expired. Slot freed.");
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
                DBConnection.commitTransaction(); 
                return session;
            }
            DBConnection.rollbackTransaction();
            return null;
        } catch (Exception e) {
            DBConnection.rollbackTransaction();
            throw e;
        }
    }

    public Bill processNextExit() throws SQLException {
        Integer vehicleId = exitQueue.poll();
        if (vehicleId == null) return null;

        try {
            DBConnection.beginTransaction(); // Start Atomic Transaction
            
            ParkingSession session = sessionDAO.getActiveSessionByVehicleId(vehicleId);
            if (session == null) {
                DBConnection.rollbackTransaction();
                return null;
            }

            LocalDateTime exitTime = LocalDateTime.now();
            sessionDAO.updateExitDetails(session.getId(), exitTime, "COMPLETED");
            slotService.updateSlotStatus(session.getSlotId(), "AVAILABLE");
            
            session.setExitTime(exitTime);
            session.setStatus("COMPLETED");
            recentSessions.push(session);

            long minutesParked = Duration.between(session.getEntryTime(), exitTime).toMinutes();
            long hoursParked = (long) Math.ceil(minutesParked / 60.0);
            if (hoursParked == 0) hoursParked = 1;

            String vehicleType = getVehicleTypeById(vehicleId);
            Bill bill = billService.generateBill(session.getId(), vehicleType, hoursParked);
            
            DBConnection.commitTransaction(); // Commit Exit details + Bill generation
            return bill;
        } catch (Exception e) {
            DBConnection.rollbackTransaction();
            throw e;
        }
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
        Connection conn = DBConnection.getConnection(); // FIX: Outside try block
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("vehicle_type");
            }
        }
        return "Car";
    }
}