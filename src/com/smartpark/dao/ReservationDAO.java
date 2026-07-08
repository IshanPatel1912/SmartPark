package com.smartpark.dao;

import com.smartpark.models.Reservation;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class ReservationDAO {

    public int createReservation(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservations (customer_id, vehicle_id, slot_id, reservation_time, expiry_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, reservation.getCustomerId());
            stmt.setInt(2, reservation.getVehicleId());
            stmt.setInt(3, reservation.getSlotId());
            stmt.setTimestamp(4, Timestamp.valueOf(reservation.getReservationTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(reservation.getExpiryTime()));
            stmt.setString(6, reservation.getStatus());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Reservation getReservationById(int id) throws SQLException {
        String query = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getInt("vehicle_id"),
                            rs.getInt("slot_id"),
                            rs.getTimestamp("reservation_time").toLocalDateTime(),
                            rs.getTimestamp("expiry_time").toLocalDateTime(),
                            rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public void updateReservationStatus(int id, String status) throws SQLException {
        String query = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}