package com.smartpark.dao;

import com.smartpark.models.Vehicle;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public void addVehicle(Vehicle vehicle) throws SQLException {
        String query = "INSERT INTO vehicles (customer_id, license_plate, vehicle_type) VALUES (?, ?, ?)";
        Connection conn = DBConnection.getConnection(); 
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicle.getCustomerId());
            stmt.setString(2, vehicle.getLicensePlate());
            stmt.setString(3, vehicle.getVehicleType());
            stmt.executeUpdate();
        }
    }

    public Vehicle getVehicleByLicensePlate(String licensePlate) throws SQLException {
        String query = "SELECT * FROM vehicles WHERE license_plate = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new Vehicle(rs.getInt("id"), rs.getInt("customer_id"), rs.getString("license_plate"), rs.getString("vehicle_type"));
            }
        }
        return null;
    }

    public List<Vehicle> getVehiclesByCustomerId(int customerId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles WHERE customer_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) vehicles.add(new Vehicle(rs.getInt("id"), rs.getInt("customer_id"), rs.getString("license_plate"), rs.getString("vehicle_type")));
            }
        }
        return vehicles;
    }
}