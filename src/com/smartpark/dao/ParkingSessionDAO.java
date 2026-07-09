package com.smartpark.dao;

import com.smartpark.models.ParkingSession;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

public class ParkingSessionDAO {

    public int createSession(ParkingSession session) throws SQLException {
        String query = "INSERT INTO parking_sessions (reservation_id, vehicle_id, slot_id, entry_time, exit_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            if (session.getReservationId() <= 0) stmt.setNull(1, Types.INTEGER);
            else stmt.setInt(1, session.getReservationId());
            
            stmt.setInt(2, session.getVehicleId());
            stmt.setInt(3, session.getSlotId());
            stmt.setTimestamp(4, Timestamp.valueOf(session.getEntryTime()));
            
            if (session.getExitTime() != null) stmt.setTimestamp(5, Timestamp.valueOf(session.getExitTime()));
            else stmt.setNull(5, Types.TIMESTAMP);
            
            stmt.setString(6, session.getStatus());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public ParkingSession getActiveSessionByVehicleId(int vehicleId) throws SQLException {
        String query = "SELECT * FROM parking_sessions WHERE vehicle_id = ? AND status = 'ACTIVE'";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSession(
                            rs.getInt("id"), rs.getInt("reservation_id"), rs.getInt("vehicle_id"), rs.getInt("slot_id"),
                            rs.getTimestamp("entry_time").toLocalDateTime(),
                            rs.getTimestamp("exit_time") != null ? rs.getTimestamp("exit_time").toLocalDateTime() : null,
                            rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public void updateExitDetails(int sessionId, java.time.LocalDateTime exitTime, String status) throws SQLException {
        String query = "UPDATE parking_sessions SET exit_time = ?, status = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(exitTime));
            stmt.setString(2, status);
            stmt.setInt(3, sessionId);
            stmt.executeUpdate();
        }
    }
}