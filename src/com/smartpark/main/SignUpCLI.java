package com.smartpark.main;

import com.smartpark.services.CustomerService;
import com.smartpark.services.EmployeeService;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SignUpCLI {

    private CustomerService customerService;
    private EmployeeService employeeService;
    private Scanner scanner;

    public SignUpCLI(CustomerService customerService, EmployeeService employeeService, Scanner scanner) {
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.scanner = scanner;
    }

    public void showSignUpMenu(boolean isAdminLoggedIn) {
        System.out.println("\n=================================");
        System.out.println("       SMARTPARK REGISTRATION    ");
        System.out.println("=================================");
        System.out.println("1. Customer Registration");
        System.out.println("2. Employee/Admin Registration");
        System.out.println("3. Back to Main Menu");
        System.out.print("Select an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                registerCustomer();
                break;
            case 2:
                registerEmployee(isAdminLoggedIn);
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void registerCustomer() {
        System.out.println("\n--- Customer Registration ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Contact Number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        try {
            customerService.registerCustomer(username, password, name, contactNumber, email);
            System.out.println("Customer registered successfully! You can now log in.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void registerEmployee(boolean isAdminLoggedIn) {
        boolean isFirstAdmin = checkIsFirstAdmin();
        
        if (!isAdminLoggedIn && !isFirstAdmin) {
            System.out.println("Registration Denied: Only a logged-in Admin can register new employees.");
            return;
        }

        if (isFirstAdmin && !isAdminLoggedIn) {
            System.out.println("\n[SYSTEM BOOTSTRAP] No Admins found. You are registering the FIRST Admin account.");
        }

        System.out.println("\n--- Employee Registration ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        // BUG FIX #2: Force Role to Admin during bootstrap
        String role;
        if (isFirstAdmin && !isAdminLoggedIn) {
            role = "Admin";
            System.out.println("Role automatically set to: Admin");
        } else {
            System.out.print("Role (Admin / Manager / Security Guard / Parking Operator): ");
            role = scanner.nextLine();
        }

        System.out.print("Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Contact Number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Shift (Morning / Evening / Night): ");
        String shift = scanner.nextLine();
        System.out.print("Salary: ₹");
        double salary = getDoubleInput();

        try {
            employeeService.registerEmployee(username, password, role, name, contactNumber, shift, java.math.BigDecimal.valueOf(salary));
            System.out.println(role + " registered successfully!");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private boolean checkIsFirstAdmin() {
        String query = "SELECT COUNT(*) FROM employees WHERE employee_type = 'Admin'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.out.println("Database error during admin check: " + e.getMessage());
        }
        return false;
    }

    private int getIntInput() {
        try { return Integer.parseInt(scanner.nextLine()); } catch (NumberFormatException e) { return -1; }
    }

    private double getDoubleInput() {
        try { return Double.parseDouble(scanner.nextLine()); } catch (NumberFormatException e) { return -1; }
    }
}