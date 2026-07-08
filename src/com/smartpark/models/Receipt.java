package com.smartpark.models;

import com.smartpark.interfaces.Printable;
import com.smartpark.utils.FileHandler;

import java.io.IOException;

public class Receipt implements Printable {

    private Bill bill;
    private ParkingSession session;
    private String customerName;
    private String licensePlate;

    public Receipt(Bill bill, ParkingSession session, String customerName, String licensePlate) {
        this.bill = bill;
        this.session = session;
        this.customerName = customerName;
        this.licensePlate = licensePlate;
    }

    @Override
    public void printDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("         SMARTPARK RECEIPT          \n");
        sb.append("====================================\n");
        sb.append("Bill ID: ").append(bill.getId()).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("License Plate: ").append(licensePlate).append("\n");
        sb.append("Entry Time: ").append(session.getEntryTime()).append("\n");
        sb.append("Exit Time: ").append(session.getExitTime()).append("\n");
        sb.append("Total Amount: $").append(bill.getAmount()).append("\n");
        sb.append("Payment Status: ").append(bill.getPaymentStatus()).append("\n");
        sb.append("====================================\n");

        String fileName = "Receipt_" + bill.getId() + "_" + licensePlate + ".txt";
        
        try {
            FileHandler.saveReceipt(fileName, sb.toString());
            FileHandler.writeLog("INFO", "Receipt generated for Bill ID: " + bill.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}