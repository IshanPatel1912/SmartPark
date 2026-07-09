package com.smartpark.dao;

import com.smartpark.models.Bill;
import com.smartpark.models.ParkingSession;
import com.smartpark.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<ParkingSession> getParkingHistoryByCustomer(int customerId) throws SQLException {
        List<ParkingSession> history = new ArrayList<>();
        String query = "SELECT ps.* FROM parking_sessions ps " +
                       "JOIN vehicles v ON ps.vehicle_id = v.id " +
                       "WHERE v.customer_id = ? ORDER BY ps.entry_time DESC";
        Connection conn = DBConnection.getConnection(); 
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new ParkingSession(rs.getInt("id"), rs.getInt("reservation_id"), rs.getInt("vehicle_id"), rs.getInt("slot_id"), 
                                                   rs.getTimestamp("entry_time").toLocalDateTime(), 
                                                   rs.getTimestamp("exit_time") != null ? rs.getTimestamp("exit_time").toLocalDateTime() : null, 
                                                   rs.getString("status")));
                }
            }
        }
        return history;
    }

    public List<Bill> getBillsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM bills WHERE billing_time >= ? AND billing_time <= ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(new Bill(rs.getInt("id"), rs.getInt("session_id"), rs.getBigDecimal("amount"), rs.getString("payment_status"), rs.getTimestamp("billing_time").toLocalDateTime()));
                }
            }
        }
        return bills;
    }

    public void printCurrentlyParkedVehicles() throws SQLException {
        String query = "SELECT ps.slot_id, s.slot_number, v.license_plate, v.vehicle_type, ps.entry_time " +
                       "FROM parking_sessions ps " +
                       "JOIN vehicles v ON ps.vehicle_id = v.id " +
                       "JOIN parking_slots s ON ps.slot_id = s.id " +
                       "WHERE ps.status = 'ACTIVE' ORDER BY s.floor_id, s.slot_number";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- Currently Parked Vehicles ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("Slot: %-8s | Plate: %-12s | Type: %-5s | Entry: %s%n", rs.getString("slot_number"), rs.getString("license_plate"), rs.getString("vehicle_type"), rs.getTimestamp("entry_time"));
            }
            if (!found) System.out.println("No vehicles currently parked.");
        }
    }

    public void printSystemCapacityStatus() throws SQLException {
        String query = "SELECT slot_type, status, COUNT(*) as count FROM parking_slots GROUP BY slot_type, status";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n--- Live System Capacity ---");
            while (rs.next()) {
                System.out.printf("Type: %-5s | Status: %-10s | Count: %d%n", rs.getString("slot_type"), rs.getString("status"), rs.getInt("count"));
            }
        }
    }

    public String getPeakHour() throws SQLException {
        String peakHour = "N/A";
        String query = "SELECT HOUR(entry_time) AS peak_hour, COUNT(*) AS count " +
                       "FROM parking_sessions GROUP BY HOUR(entry_time) ORDER BY count DESC LIMIT 1";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                peakHour = rs.getInt("peak_hour") + ":00 - " + (rs.getInt("peak_hour") + 1) + ":00";
            }
        }
        return peakHour;
    }
}