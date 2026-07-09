package com.smartpark.services;

import com.smartpark.dao.EmployeeDAO;
import com.smartpark.dao.UserDAO;
import com.smartpark.exceptions.DuplicateEntityException;
import com.smartpark.exceptions.SystemException;
import com.smartpark.models.*;
import com.smartpark.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private UserDAO userDAO;
    private EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.userDAO = new UserDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    // SIGNATURE UPDATED FOR BIGDECIMAL
    public void registerEmployee(String username, String password, String role, String name, String contactNumber, String shift, BigDecimal salary) throws DuplicateEntityException, SQLException, SystemException {
        try {
            DBConnection.beginTransaction(); // Start Atomic Transaction
            
            User newUser = new User(0, username, password, role);
            int userId = userDAO.registerUser(newUser);
            
            if (userId > 0) {
                Employee employee;
                if ("Manager".equalsIgnoreCase(role)) {
                    employee = new Manager(userId, name, contactNumber, shift, salary);
                } else if ("Security Guard".equalsIgnoreCase(role)) {
                    employee = new SecurityGuard(userId, name, contactNumber, shift, salary);
                } else if ("Admin".equalsIgnoreCase(role)) {
                    employee = new Admin(userId, name, contactNumber, shift, salary);
                } else {
                    employee = new ParkingOperator(userId, name, contactNumber, shift, salary);
                }
                employeeDAO.addEmployee(employee);
                DBConnection.commitTransaction(); // Save changes permanently
            } else {
                DBConnection.rollbackTransaction();
                throw new SystemException("Failed to register employee system user.");
            }
        } catch (Exception e) {
            DBConnection.rollbackTransaction(); // Undo all if error occurs
            throw e; 
        }
    }

    public Employee getEmployeeDetails(int userId) throws SQLException {
        return employeeDAO.getEmployeeByUserId(userId);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.getAllEmployees();
    }

    public void updateEmployee(Employee employee) throws SQLException {
        employeeDAO.updateEmployee(employee);
    }

    public void deleteEmployee(int userId) throws SQLException {
        employeeDAO.deleteEmployee(userId);
    }
}