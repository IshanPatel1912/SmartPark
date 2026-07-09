package com.smartpark.dao;

import com.smartpark.models.SalaryDisbursement;
import com.smartpark.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {

    public void recordSalaryPayment(int employeeId, BigDecimal amount, LocalDate paymentDate) throws SQLException {
        String query = "INSERT INTO salary_disbursements (employee_id, amount, payment_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            stmt.setBigDecimal(2, amount); // BIGDECIMAL
            stmt.setDate(3, Date.valueOf(paymentDate));
            stmt.executeUpdate();
        }
    }

    public List<SalaryDisbursement> getDisbursementsBetweenDates(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<SalaryDisbursement> disbursements = new ArrayList<>();
        String query = "SELECT * FROM salary_disbursements WHERE payment_date >= ? AND payment_date <= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    disbursements.add(new SalaryDisbursement(
                            rs.getInt("id"),
                            rs.getInt("employee_id"),
                            rs.getBigDecimal("amount"), // BIGDECIMAL
                            rs.getDate("payment_date").toLocalDate()
                    ));
                }
            }
        }
        return disbursements;
    }
}