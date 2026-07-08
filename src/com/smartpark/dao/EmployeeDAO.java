package com.smartpark.dao;

import com.smartpark.models.Admin;
import com.smartpark.models.Employee;
import com.smartpark.models.Manager;
import com.smartpark.models.ParkingOperator;
import com.smartpark.models.SecurityGuard;
import com.smartpark.utils.DBConnection;
import com.smartpark.exceptions.DuplicateEntityException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public void addEmployee(Employee employee) throws DuplicateEntityException, SQLException {
        String query = "INSERT INTO employees (user_id, name, contact_number, employee_type, shift, salary) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employee.getUserId());
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getContactNumber());
            stmt.setString(4, employee.getEmployeeType());
            stmt.setString(5, employee.getShift());
            stmt.setDouble(6, employee.getSalary());
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntityException("Employee with this contact number already exists.");
        }
    }

    public Employee getEmployeeByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM employees WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        }
        return null;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        }
        return employees;
    }

    public void updateEmployee(Employee employee) throws SQLException {
        String query = "UPDATE employees SET name = ?, contact_number = ?, shift = ?, salary = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getContactNumber());
            stmt.setString(3, employee.getShift());
            stmt.setDouble(4, employee.getSalary());
            stmt.setInt(5, employee.getUserId());
            stmt.executeUpdate();
        }
    }

    public void deleteEmployee(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?"; // Cascade delete will remove from employees table
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        String type = rs.getString("employee_type");
        if ("Manager".equalsIgnoreCase(type)) {
            return new Manager(rs.getInt("user_id"), rs.getString("name"), rs.getString("contact_number"), rs.getString("shift"), rs.getDouble("salary"));
        } else if ("Security Guard".equalsIgnoreCase(type)) {
            return new SecurityGuard(rs.getInt("user_id"), rs.getString("name"), rs.getString("contact_number"), rs.getString("shift"), rs.getDouble("salary"));
        } else if ("Admin".equalsIgnoreCase(type)) {
            return new Admin(rs.getInt("user_id"), rs.getString("name"), rs.getString("contact_number"), rs.getString("shift"), rs.getDouble("salary"));
        } else {
            return new ParkingOperator(rs.getInt("user_id"), rs.getString("name"), rs.getString("contact_number"), rs.getString("shift"), rs.getDouble("salary"));
        }
    }
}