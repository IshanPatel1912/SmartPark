package com.smartpark.dao;

import com.smartpark.models.ParkingSlot;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParkingSlotDAO {

    public void addSlot(ParkingSlot slot) throws SQLException {
        String query = "INSERT INTO parking_slots (floor_id, slot_number, slot_type, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slot.getFloorId());
            stmt.setString(2, slot.getSlotNumber());
            stmt.setString(3, slot.getSlotType());
            stmt.setString(4, slot.getStatus());
            stmt.executeUpdate();
        }
    }

    public ParkingSlot getNearestAvailableSlot(String slotType) throws SQLException {
        String query = "SELECT * FROM parking_slots WHERE slot_type = ? AND status = 'AVAILABLE' ORDER BY floor_id ASC, id ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, slotType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSlot(
                            rs.getInt("id"), rs.getInt("floor_id"),
                            rs.getString("slot_number"), rs.getString("slot_type"), rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public List<ParkingSlot> getAvailableSlots(String slotType) throws SQLException {
        List<ParkingSlot> slots = new ArrayList<>();
        String query = "SELECT * FROM parking_slots WHERE slot_type = ? AND status = 'AVAILABLE' ORDER BY floor_id ASC, id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, slotType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(new ParkingSlot(
                            rs.getInt("id"), rs.getInt("floor_id"),
                            rs.getString("slot_number"), rs.getString("slot_type"), rs.getString("status")
                    ));
                }
            }
        }
        return slots;
    }

    public List<ParkingSlot> getSlotsByFloorAndType(int floorId, String slotType) throws SQLException {
        List<ParkingSlot> slots = new ArrayList<>();
        String query = "SELECT * FROM parking_slots WHERE floor_id = ? AND slot_type = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, floorId);
            stmt.setString(2, slotType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(new ParkingSlot(
                            rs.getInt("id"), rs.getInt("floor_id"),
                            rs.getString("slot_number"), rs.getString("slot_type"), rs.getString("status")
                    ));
                }
            }
        }
        return slots;
    }

    public ParkingSlot getSlotById(int id) throws SQLException {
        String query = "SELECT * FROM parking_slots WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSlot(
                            rs.getInt("id"), rs.getInt("floor_id"),
                            rs.getString("slot_number"), rs.getString("slot_type"), rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public void updateSlotStatus(int slotId, String status) throws SQLException {
        String query = "UPDATE parking_slots SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, slotId);
            stmt.executeUpdate();
        }
    }
}