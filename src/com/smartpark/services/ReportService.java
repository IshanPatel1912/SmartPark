package com.smartpark.services;

import com.smartpark.dao.ReportDAO;
import com.smartpark.interfaces.ReportGenerator;
import com.smartpark.models.Bill;
import com.smartpark.models.ParkingSession;
import com.smartpark.utils.FileHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReportService implements ReportGenerator<Bill> {

    private ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    public List<ParkingSession> getCustomerParkingHistory(int customerId) throws SQLException {
        return reportDAO.getParkingHistoryByCustomer(customerId);
    }

    public String analyzePeakHours() throws SQLException {
        return reportDAO.getPeakHour();
    }

    public void generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate, String fileName) throws Exception {
        List<Bill> bills = reportDAO.getBillsBetweenDates(startDate, endDate);
        generateReport(bills, fileName);
    }

    public void viewCurrentlyParkedVehicles() throws SQLException {
        reportDAO.printCurrentlyParkedVehicles();
    }

    public void viewSystemCapacity() throws SQLException {
        reportDAO.printSystemCapacityStatus();
    }

    @Override
    public void generateReport(List<Bill> data, String fileName) throws Exception {
        double totalRevenue = 0.0;
        int paidCount = 0;
        int pendingCount = 0;

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("=== REVENUE REPORT ===\n");
        reportContent.append("Generated On: ").append(LocalDateTime.now()).append("\n\n");

        for (Bill bill : data) {
            reportContent.append("Bill ID: ").append(bill.getId())
                         .append(" | Amount: $").append(bill.getAmount())
                         .append(" | Status: ").append(bill.getPaymentStatus())
                         .append(" | Date: ").append(bill.getBillingTime()).append("\n");
            
            if ("PAID".equalsIgnoreCase(bill.getPaymentStatus())) {
                totalRevenue += bill.getAmount();
                paidCount++;
            } else {
                pendingCount++;
            }
        }

        reportContent.append("\n=== SUMMARY ===\n");
        reportContent.append("Total Bills: ").append(data.size()).append("\n");
        reportContent.append("Paid Bills: ").append(paidCount).append("\n");
        reportContent.append("Pending Bills: ").append(pendingCount).append("\n");
        reportContent.append("Total Revenue Collected: $").append(totalRevenue).append("\n");

        FileHandler.saveReport(fileName, reportContent.toString());
        FileHandler.writeLog("INFO", "Revenue report generated: " + fileName);
    }
}