package com.smartpark.dao;

import com.smartpark.models.ParkingFloor;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParkingFloorDAO {

    public int addFloor(ParkingFloor floor) throws SQLException {
        String query = "INSERT INTO parking_floors (floor_number, total_capacity) VALUES (?, ?)";
        Connection conn = DBConnection.getConnection(); 
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, floor.getFloorNumber());
            stmt.setInt(2, floor.getTotalCapacity());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<ParkingFloor> getAllFloors() throws SQLException {
        List<ParkingFloor> floors = new ArrayList<>();
        String query = "SELECT * FROM parking_floors";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                floors.add(new ParkingFloor(rs.getInt("id"), rs.getInt("floor_number"), rs.getInt("total_capacity")));
            }
        }
        return floors;
    }
}