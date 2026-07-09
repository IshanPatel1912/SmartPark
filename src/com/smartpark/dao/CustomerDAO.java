package com.smartpark.dao;

import com.smartpark.models.Customer;
import com.smartpark.utils.DBConnection;
import com.smartpark.exceptions.DuplicateEntityException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class CustomerDAO {

    public void addCustomer(Customer customer) throws DuplicateEntityException, SQLException {
        String query = "INSERT INTO customers (user_id, name, contact_number, email) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customer.getUserId());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getContactNumber());
            stmt.setString(4, customer.getEmail());
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntityException("Customer with this contact number or email already exists.");
        }
    }

    public Customer getCustomerByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM customers WHERE user_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getInt("user_id"), rs.getString("name"), rs.getString("contact_number"), rs.getString("email"));
                }
            }
        }
        return null;
    }
}