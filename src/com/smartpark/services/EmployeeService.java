package com.smartpark.services;

import com.smartpark.dao.EmployeeDAO;
import com.smartpark.dao.UserDAO;
import com.smartpark.exceptions.DuplicateEntityException;
import com.smartpark.exceptions.SystemException;
import com.smartpark.models.Admin;
import com.smartpark.models.Employee;
import com.smartpark.models.Manager;
import com.smartpark.models.ParkingOperator;
import com.smartpark.models.SecurityGuard;
import com.smartpark.models.User;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private UserDAO userDAO;
    private EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.userDAO = new UserDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    public void registerEmployee(String username, String password, String role, String name, String contactNumber, String shift, double salary) throws DuplicateEntityException, SQLException, SystemException {
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
        } else {
            throw new SystemException("Failed to register employee system user.");
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