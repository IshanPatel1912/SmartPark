package com.smartpark.main;

import com.smartpark.services.ParkingFloorService;
import com.smartpark.services.ParkingSlotService;
import com.smartpark.services.PricingService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

public class SetupWizard {

    private ParkingFloorService floorService;
    private ParkingSlotService slotService;
    private PricingService pricingService;
    private Scanner scanner;

    public SetupWizard(ParkingFloorService floorService, ParkingSlotService slotService, PricingService pricingService, Scanner scanner) {
        this.floorService = floorService;
        this.slotService = slotService;
        this.pricingService = pricingService;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n=== SMARTPARK FIRST-TIME SETUP WIZARD ===");
        try {
            setupFloorsAndSlots();
            setupPricing();
            System.out.println("\n>>> Setup Complete! System is ready. <<<");
        } catch (Exception e) {
            System.out.println("Setup failed: " + e.getMessage());
        }
    }

    private void setupFloorsAndSlots() throws SQLException {
        System.out.print("How many parking floors does your facility have? ");
        int numFloors = getIntInput();

        for (int i = 1; i <= numFloors; i++) {
            System.out.println("\n--- Configuration for Floor " + i + " ---");
            System.out.print("Number of BIKE slots: ");
            int bikes = getIntInput();
            System.out.print("Number of CAR slots: ");
            int cars = getIntInput();
            System.out.print("Number of SUV slots: ");
            int suvs = getIntInput();
            System.out.print("Number of EV slots: ");
            int evs = getIntInput();

            int totalCapacity = bikes + cars + suvs + evs;
            int floorId = floorService.addFloor(i, totalCapacity);
            
            System.out.println("Generating slots in the database...");
            slotService.generateSlotsForFloor(floorId, i, bikes, cars, suvs, evs);
            System.out.println("Floor " + i + " configured successfully with " + totalCapacity + " total slots.");
        }
    }

    private void setupPricing() throws SQLException {
        System.out.println("\n--- Pricing Configuration ---");
        String[] types = {"Bike", "Car", "SUV", "EV"};
        
        for (String type : types) {
            System.out.println("Pricing for " + type + ":");
            System.out.print("  Base Rate (Fixed Entry Fee): ₹");
            double base = getDoubleInput();
            System.out.print("  Hourly Rate: ₹");
            double hourly = getDoubleInput();
            pricingService.setPricing(type, BigDecimal.valueOf(base), BigDecimal.valueOf(hourly));
        }
    }

    private int getIntInput() {
        while (true) {
            try { return Integer.parseInt(scanner.nextLine()); } 
            catch (NumberFormatException e) { System.out.print("Invalid input. Enter a number: "); }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try { return Double.parseDouble(scanner.nextLine()); } 
            catch (NumberFormatException e) { System.out.print("Invalid input. Enter a valid price: "); }
        }
    }
}