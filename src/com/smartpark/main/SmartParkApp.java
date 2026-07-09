package com.smartpark.main;

import com.smartpark.models.*;
import com.smartpark.services.*;
import com.smartpark.utils.DatabaseInitializer;
import com.smartpark.utils.FileHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class SmartParkApp {

    private static Scanner scanner = new Scanner(System.in);
    
    private static AuthenticationService authService;
    private static CustomerService customerService;
    private static EmployeeService employeeService;
    private static ParkingFloorService floorService;
    private static ParkingSlotService slotService;
    private static PricingService pricingService;
    private static VehicleService vehicleService;
    private static ReservationService reservationService;
    private static BillService billService;
    private static ParkingSessionService sessionService;
    private static ReportService reportService;
    private static SalaryService salaryService;
    private static FeedbackService feedbackService;
    private static SignUpCLI signUpCLI;
    private static SetupWizard setupWizard;

    public static void main(String[] args) {
        System.out.println("Initializing system and verifying database...");
        FileHandler.initializeDirectories();
        DatabaseInitializer.initializeDatabase();
        
        initializeServices();

        try {
            if (floorService.getAllFloors().isEmpty()) {
                setupWizard.start();
            }
        } catch (Exception e) {
            System.out.println("Failed to check system setup status: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=============================================");
            System.out.println(" SMART MULTI-LEVEL PARKING MANAGEMENT SYSTEM ");
            System.out.println("=============================================");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit System");
            System.out.print("Select an option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1: handleLogin(); break;
                case 2: signUpCLI.showSignUpMenu(false); break;
                case 3: running = false; System.out.println("Shutting down SmartPark System. Goodbye!"); break;
                default: System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void initializeServices() {
        authService = new AuthenticationService();
        customerService = new CustomerService();
        employeeService = new EmployeeService();
        floorService = new ParkingFloorService();
        slotService = new ParkingSlotService();
        pricingService = new PricingService();
        vehicleService = new VehicleService();
        reservationService = new ReservationService(slotService);
        billService = new BillService(pricingService);
        sessionService = new ParkingSessionService(slotService, billService);
        reportService = new ReportService();
        salaryService = new SalaryService();
        feedbackService = new FeedbackService();
        signUpCLI = new SignUpCLI(customerService, employeeService, scanner);
        setupWizard = new SetupWizard(floorService, slotService, pricingService, scanner);
    }

    private static void handleLogin() {
        System.out.println("\n--- System Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = authService.login(username, password);
            System.out.println("\nLogin successful! Welcome, " + user.getUsername());
            
            switch (user.getRole().toLowerCase()) {
                case "customer": customerMenu(user.getId()); break;
                case "admin": adminMenu(user.getId()); break;
                case "manager": managerMenu(user.getId()); break;
                case "security guard": securityMenu(user.getId()); break;
                case "parking operator": operatorMenu(user.getId()); break;
                default: System.out.println("Role not recognized."); authService.logout(user.getId());
            }
        } catch (Exception e) {
            System.out.println("Login Failed: " + e.getMessage());
        }
    }

    private static void adminMenu(int userId) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. View All Employees");
            System.out.println("2. Add New Employee");
            System.out.println("3. Update Employee Details");
            System.out.println("4. Remove Employee");
            System.out.println("5. Disburse Salary");
            System.out.println("6. View Salary Report (Monthly)");
            System.out.println("7. View Customer Feedback");
            System.out.println("8. Logout");
            System.out.print("Select: ");
            
            int choice = getIntInput();
            try {
                switch (choice) {
                    case 1:
                        System.out.println("\n--- Employee List ---");
                        for (Employee e : employeeService.getAllEmployees()) {
                            System.out.printf("ID: %d | Name: %-15s | Role: %-15s | Shift: %-10s | Salary: ₹%.2f%n", 
                                    e.getUserId(), e.getName(), e.getEmployeeType(), e.getShift(), e.getSalary());
                        }
                        break;
                    case 2:
                        signUpCLI.showSignUpMenu(true); 
                        break;
                    // Inside adminMenu -> case 3:
                    case 3:
                        System.out.print("Enter Employee ID to update: ");
                        int empId = getIntInput();
                        Employee emp = employeeService.getEmployeeDetails(empId);
                        if (emp == null) { System.out.println("Employee not found."); break; }
                        
                        System.out.print("New Name (Leave blank to keep '" + emp.getName() + "'): ");
                        String nName = scanner.nextLine();
                        if (!nName.isEmpty()) emp.setName(nName);
                        
                        System.out.print("New Contact (Leave blank to keep '" + emp.getContactNumber() + "'): ");
                        String nContact = scanner.nextLine();
                        if (!nContact.isEmpty()) emp.setContactNumber(nContact);
                        
                        System.out.print("New Salary (Enter 0 to keep '₹" + emp.getSalary() + "'): ");
                        double nSal = getDoubleInput();
                        
                        // FIX: Wrap nSal in BigDecimal.valueOf()
                        if (nSal > 0) emp.setSalary(java.math.BigDecimal.valueOf(nSal));
                        
                        employeeService.updateEmployee(emp);
                        System.out.println("Employee updated successfully.");
                        break;
                    case 4:
                        System.out.print("Enter Employee ID to delete: ");
                        employeeService.deleteEmployee(getIntInput());
                        System.out.println("Employee removed from system.");
                        break;
                    case 5:
                        System.out.print("Enter Employee ID to pay: ");
                        int pId = getIntInput();
                        System.out.print("Amount to disburse: ₹");
                        double pAmt = getDoubleInput();
                        // FIX: Convert double to BigDecimal
                        salaryService.payEmployee(pId, java.math.BigDecimal.valueOf(pAmt), LocalDate.now());
                        System.out.println("Salary of ₹" + pAmt + " disbursed successfully.");
                        break;
                    case 6:
                        System.out.print("Enter Year (e.g., 2024): ");
                        int yr = getIntInput();
                        System.out.print("Enter Month (1-12): ");
                        int mo = getIntInput();
                        LocalDate start = LocalDate.of(yr, mo, 1);
                        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
                        
                        List<SalaryDisbursement> salaries = salaryService.getSalaryReport(start, end);
                        // FIX: Use BigDecimal for Math
                        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                        System.out.println("\n--- Salary Report (" + start.getMonth() + " " + yr + ") ---");
                        for (SalaryDisbursement s : salaries) {
                            System.out.printf("Emp ID: %d | Date: %s | Amount: ₹%.2f%n", s.getEmployeeId(), s.getPaymentDate(), s.getAmount());
                            total = total.add(s.getAmount());
                        }
                        System.out.println("Total Salary Paid: ₹" + total);
                        break;
                    case 7:
                        feedbackService.printAggregateFeedback();
                        break;
                    case 8:
                        authService.logout(userId);
                        active = false;
                        break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Admin Error: " + e.getMessage());
            }
        }
    }

    private static void managerMenu(int userId) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Manager Dashboard ---");
            System.out.println("1. View Live System Capacity");
            System.out.println("2. View Currently Parked Vehicles");
            System.out.println("3. Analyze Peak Hours");
            System.out.println("4. Generate Daily Revenue Report");
            System.out.println("5. Update Vehicle Pricing");
            System.out.println("6. Add Extra Slots to a Floor");
            System.out.println("7. View Customer Feedback");
            System.out.println("8. Logout");
            System.out.print("Select: ");
            
            int choice = getIntInput();
            try {
                switch (choice) {
                    case 1: reportService.viewSystemCapacity(); break;
                    case 2: reportService.viewCurrentlyParkedVehicles(); break;
                    case 3:
                        System.out.println("System Peak Hours identified: " + reportService.analyzePeakHours());
                        break;
                    case 4:
                        java.time.LocalDateTime start = java.time.LocalDateTime.now().minusDays(1);
                        java.time.LocalDateTime end = java.time.LocalDateTime.now();
                        reportService.generateRevenueReport(start, end, "Daily_Revenue_" + java.time.LocalDate.now() + ".txt");
                        System.out.println("Report generated in data/backups/");
                        break;
                    case 5:
                        System.out.print("Enter Vehicle Type to update (Bike/Car/SUV/EV): ");
                        String type = scanner.nextLine();
                        System.out.print("New Base Rate: ₹");
                        double base = getDoubleInput();
                        System.out.print("New Hourly Rate: ₹");
                        double hourly = getDoubleInput();
                        // FIX: Convert double to BigDecimal
                        pricingService.setPricing(type, java.math.BigDecimal.valueOf(base), java.math.BigDecimal.valueOf(hourly));
                        System.out.println("Pricing updated successfully!");
                        break;
                    case 6:
                        System.out.print("Enter Floor ID (Internal DB ID): ");
                        int fId = getIntInput();
                        System.out.print("Enter Floor Number (Visible Level): ");
                        int fNum = getIntInput();
                        System.out.print("Enter Vehicle Type (Bike/Car/SUV/EV): ");
                        String vType = scanner.nextLine();
                        System.out.print("How many new slots to add? ");
                        int nSlots = getIntInput();
                        slotService.addExtraSlotsToFloor(fId, fNum, vType, nSlots);
                        break;
                    case 7:
                        feedbackService.printAggregateFeedback();
                        break;
                    case 8:
                        authService.logout(userId);
                        active = false;
                        break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Manager Error: " + e.getMessage());
            }
        }
    }

    private static void securityMenu(int userId) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Security Guard Dashboard ---");
            System.out.println("1. Scan License Plate for Entry (Add to Queue)");
            System.out.println("2. Process Next Entry (Auto-Allocate Nearest Slot)");
            System.out.println("3. Scan License Plate for Exit (Add to Queue)");
            System.out.println("4. Process Next Exit & Generate Bill");
            System.out.println("5. Logout");
            System.out.print("Select: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    System.out.print("Scan License Plate at Gate (Entry): ");
                    String entryPlate = scanner.nextLine();
                    try {
                        Vehicle vehicle = vehicleService.getVehicleByLicensePlate(entryPlate);
                        if (vehicle != null) {
                            sessionService.addVehicleToEntryQueue(vehicle.getId());
                            System.out.println("Vehicle " + entryPlate + " verified and added to entry queue.");
                        } else {
                            System.out.println("ACCESS DENIED: Unregistered License Plate.");
                        }
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 2:
                    try {
                        System.out.print("Enter Reservation ID (or 0 if walk-in): ");
                        int resId = getIntInput();
                        ParkingSession session = sessionService.processNextEntry(resId);
                        if (session != null) {
                            System.out.println(">>> SMART ALLOCATION SUCCESSFUL <<<");
                            System.out.println("Assigned Slot ID: " + session.getSlotId() + " for Session ID: " + session.getId());
                            System.out.println("Gate Opening... Vehicle may proceed.");
                        } else {
                            System.out.println("Entry queue is empty.");
                        }
                    } catch (Exception e) { System.out.println("Allocation Failed: " + e.getMessage()); }
                    break;
                case 3:
                    System.out.print("Scan License Plate at Gate (Exit): ");
                    String exitPlate = scanner.nextLine();
                    try {
                        Vehicle vehicle = vehicleService.getVehicleByLicensePlate(exitPlate);
                        if (vehicle != null) {
                            sessionService.addVehicleToExitQueue(vehicle.getId());
                            System.out.println("Vehicle " + exitPlate + " verified and added to exit queue.");
                        } else {
                            System.out.println("ERROR: License plate not found in database.");
                        }
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 4:
                    try {
                        com.smartpark.models.Bill bill = sessionService.processNextExit();
                        if (bill != null) {
                            System.out.println(">>> EXIT PROCESSED <<<");
                            System.out.println("Bill Generated! Amount: ₹" + bill.getAmount() + " | Bill ID: " + bill.getId());
                        } else {
                            System.out.println("Exit queue is empty or session not found.");
                        }
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 5:
                    authService.logout(userId);
                    active = false;
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void operatorMenu(int userId) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Parking Operator Dashboard ---");
            System.out.println("1. Process Payment");
            System.out.println("2. Undo Last Exit Operation");
            System.out.println("3. Logout");
            System.out.print("Select: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    System.out.print("Enter Bill ID to process payment: ");
                    try {
                        int billId = getIntInput();
                        billService.processPayment(billId);
                        System.out.println("Payment processed successfully.");
                        billService.printReceipt(billId);
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 2:
                    try {
                        ParkingSession restored = sessionService.undoLastExit();
                        if (restored != null) System.out.println("Last exit undone for Session ID: " + restored.getId());
                        else System.out.println("No recent exits to undo.");
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 3:
                    authService.logout(userId);
                    active = false;
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void customerMenu(int userId) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Customer Dashboard ---");
            System.out.println("1. Register Vehicle");
            System.out.println("2. View Available Slots");
            System.out.println("3. Reserve a Parking Slot");
            System.out.println("4. Submit Feedback");
            System.out.println("5. Logout");
            System.out.print("Select: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    System.out.print("Enter License Plate: ");
                    String lp = scanner.nextLine();
                    System.out.print("Enter Vehicle Type (Car/Bike/SUV/EV): ");
                    String type = scanner.nextLine();
                    try {
                        vehicleService.registerVehicle(userId, lp, type);
                        System.out.println("Vehicle registered successfully.");
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 2:
                    System.out.print("Enter Vehicle Type to check availability: ");
                    String vType = scanner.nextLine();
                    try {
                        slotService.getAvailableSlots(vType).forEach(slot -> 
                            System.out.println("Slot ID: " + slot.getId() + " | Number: " + slot.getSlotNumber() + " | Floor: " + slot.getFloorId())
                        );
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 3:
                    System.out.println("\n--- Reserve a Slot ---");
                    try {
                        List<Vehicle> myVehicles = vehicleService.getCustomerVehicles(userId);
                        if (myVehicles.isEmpty()) {
                            System.out.println("You have no registered vehicles. Please register a vehicle first.");
                            break;
                        }
                        
                        System.out.println("Your Registered Vehicles:");
                        for (Vehicle v : myVehicles) {
                            System.out.println("ID: " + v.getId() + " | Plate: " + v.getLicensePlate() + " | Type: " + v.getVehicleType());
                        }
                        
                        System.out.print("Enter the Vehicle ID you want to park: ");
                        int vId = getIntInput();
                        
                        Vehicle selectedVehicle = null;
                        for (Vehicle v : myVehicles) {
                            if (v.getId() == vId) selectedVehicle = v;
                        }
                        
                        if (selectedVehicle == null) {
                            System.out.println("Invalid Vehicle ID.");
                            break;
                        }

                        System.out.print("How many hours do you want to reserve the slot for? ");
                        int duration = getIntInput();

                        ParkingSlot slot = slotService.getNearestAvailableSlot(selectedVehicle.getVehicleType());
                        if (slot == null) {
                            System.out.println("Sorry, no available slots for " + selectedVehicle.getVehicleType() + " at the moment.");
                        } else {
                            int resId = reservationService.createReservation(userId, selectedVehicle.getId(), slot.getId(), duration);
                            System.out.println("\n>>> RESERVATION SUCCESSFUL <<<");
                            System.out.println("Reservation ID: " + resId);
                            System.out.println("Reserved Slot: " + slot.getSlotNumber() + " on Floor " + slot.getFloorId());
                            System.out.println("Please show Reservation ID " + resId + " to the Security Guard upon arrival.");
                        }
                    } catch (Exception e) {
                        System.out.println("Reservation failed: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Rate your experience (1-5): ");
                    int rating = getIntInput();
                    System.out.print("Leave a comment (optional): ");
                    String comments = scanner.nextLine();
                    try {
                        feedbackService.submitFeedback(userId, rating, comments);
                        System.out.println("Thank you for your feedback!");
                    } catch (Exception e) {
                        System.out.println("Error submitting feedback: " + e.getMessage());
                    }
                    break;
                case 5:
                    authService.logout(userId);
                    active = false;
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static int getIntInput() {
        try { return Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) { return -1; }
    }

    private static double getDoubleInput() {
        try { return Double.parseDouble(scanner.nextLine()); } catch (NumberFormatException e) { return -1; }
    }
}