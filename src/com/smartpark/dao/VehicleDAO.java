package com.smartpark.dao;

import com.smartpark.models.Vehicle;
import com.smartpark.utils.DBConnection;
import com.smartpark.exceptions.DuplicateEntityException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public void addVehicle(Vehicle vehicle) throws DuplicateEntityException, SQLException {
        String query = "INSERT INTO vehicles (customer_id, license_plate, vehicle_type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicle.getCustomerId());
            stmt.setString(2, vehicle.getLicensePlate());
            stmt.setString(3, vehicle.getVehicleType());
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntityException("Vehicle with this license plate already exists.");
        }
    }

    public Vehicle getVehicleByLicensePlate(String licensePlate) throws SQLException {
        String query = "SELECT * FROM vehicles WHERE license_plate = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Vehicle(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getString("license_plate"),
                            rs.getString("vehicle_type")
                    );
                }
            }
        }
        return null;
    }

    public List<Vehicle> getVehiclesByCustomerId(int customerId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(new Vehicle(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getString("license_plate"),
                            rs.getString("vehicle_type")
                    ));
                }
            }
        }
        return vehicles;
    }
}