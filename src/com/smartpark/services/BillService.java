package com.smartpark.services;

import com.smartpark.dao.BillDAO;
import com.smartpark.models.Bill;
import com.smartpark.models.ParkingSession;
import com.smartpark.models.Pricing;
import com.smartpark.models.Receipt;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class BillService {

    private BillDAO billDAO;
    private PricingService pricingService;

    public BillService(PricingService pricingService) {
        this.billDAO = new BillDAO();
        this.pricingService = pricingService;
    }

    public Bill generateBill(int sessionId, String vehicleType, long hoursParked) throws SQLException {
        Pricing pricing = pricingService.getPricingDetails(vehicleType);
        
        if (pricing == null) {
            throw new SQLException("Pricing details not found for vehicle type: " + vehicleType + ". Please set up pricing first.");
        }
        
        double amount = pricing.getBaseRate();
        if (hoursParked > 1) {
            amount += pricing.getHourlyRate() * (hoursParked - 1);
        }

        Bill bill = new Bill(0, sessionId, amount, "PENDING", LocalDateTime.now());
        int billId = billDAO.createBill(bill);
        
        if (billId > 0) {
            bill.setId(billId);
        }
        
        return bill;
    }

    public void processPayment(int billId) throws SQLException {
        billDAO.updatePaymentStatus(billId, "PAID");
    }

    public void printReceipt(int billId) throws SQLException {
        String query = "SELECT b.amount, b.payment_status, b.billing_time, " +
                       "ps.id AS session_id, ps.entry_time, ps.exit_time, " +
                       "v.license_plate, c.name " +
                       "FROM bills b " +
                       "JOIN parking_sessions ps ON b.session_id = ps.id " +
                       "JOIN vehicles v ON ps.vehicle_id = v.id " +
                       "JOIN customers c ON v.customer_id = c.user_id " +
                       "WHERE b.id = ?";
                       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill(billId, rs.getInt("session_id"), rs.getDouble("amount"), 
                                         rs.getString("payment_status"), rs.getTimestamp("billing_time").toLocalDateTime());
                    
                    ParkingSession session = new ParkingSession(rs.getInt("session_id"), 0, 0, 0, 
                                                                rs.getTimestamp("entry_time").toLocalDateTime(), 
                                                                rs.getTimestamp("exit_time") != null ? rs.getTimestamp("exit_time").toLocalDateTime() : null, 
                                                                "COMPLETED");
                    
                    String customerName = rs.getString("name");
                    String licensePlate = rs.getString("license_plate");
                    
                    Receipt receipt = new Receipt(bill, session, customerName, licensePlate);
                    receipt.printDetails();
                    System.out.println("Receipt successfully saved to data/receipts/");
                }
            }
        }
    }
}