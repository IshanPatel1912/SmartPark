package com.smartpark.services;

import com.smartpark.dao.BillDAO;
import com.smartpark.models.Bill;
import com.smartpark.models.Pricing;

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
}