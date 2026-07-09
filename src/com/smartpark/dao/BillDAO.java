package com.smartpark.dao;

import com.smartpark.models.Bill;
import com.smartpark.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class BillDAO {

    public int createBill(Bill bill) throws SQLException {
        String query = "INSERT INTO bills (session_id, amount, payment_status, billing_time) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection(); 
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bill.getSessionId());
            stmt.setBigDecimal(2, bill.getAmount());
            stmt.setString(3, bill.getPaymentStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(bill.getBillingTime()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public void updatePaymentStatus(int billId, String paymentStatus) throws SQLException {
        String query = "UPDATE bills SET payment_status = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, paymentStatus);
            stmt.setInt(2, billId);
            stmt.executeUpdate();
        }
    }
}