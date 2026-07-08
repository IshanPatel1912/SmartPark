package com.smartpark.dao;

import com.smartpark.models.Pricing;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PricingDAO {

    public void addOrUpdatePricing(Pricing pricing) throws SQLException {
        String query = "INSERT INTO pricing (vehicle_type, base_rate, hourly_rate) VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE base_rate = VALUES(base_rate), hourly_rate = VALUES(hourly_rate)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, pricing.getVehicleType());
            stmt.setDouble(2, pricing.getBaseRate());
            stmt.setDouble(3, pricing.getHourlyRate());
            stmt.executeUpdate();
        }
    }

    public Pricing getPricingByVehicleType(String vehicleType) throws SQLException {
        String query = "SELECT * FROM pricing WHERE vehicle_type = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vehicleType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Pricing(
                            rs.getInt("id"), rs.getString("vehicle_type"),
                            rs.getDouble("base_rate"), rs.getDouble("hourly_rate")
                    );
                }
            }
        }
        return null;
    }
}