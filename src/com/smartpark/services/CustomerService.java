package com.smartpark.services;

import com.smartpark.dao.CustomerDAO;
import com.smartpark.dao.UserDAO;
import com.smartpark.exceptions.DuplicateEntityException;
import com.smartpark.exceptions.SystemException;
import com.smartpark.models.Customer;
import com.smartpark.models.User;
import com.smartpark.utils.DBConnection;

import java.sql.SQLException;

public class CustomerService {

    private UserDAO userDAO;
    private CustomerDAO customerDAO;

    public CustomerService() {
        this.userDAO = new UserDAO();
        this.customerDAO = new CustomerDAO();
    }

    public void registerCustomer(String username, String password, String name, String contactNumber, String email) throws DuplicateEntityException, SQLException, SystemException {
        try {
            DBConnection.beginTransaction(); // Start Atomic Transaction
            
            User newUser = new User(0, username, password, "Customer");
            int userId = userDAO.registerUser(newUser);
            
            if (userId > 0) {
                Customer customer = new Customer(userId, name, contactNumber, email);
                customerDAO.addCustomer(customer);
                DBConnection.commitTransaction(); // Save changes permanently
            } else {
                DBConnection.rollbackTransaction(); 
                throw new SystemException("Failed to register customer system user.");
            }
        } catch (Exception e) {
            DBConnection.rollbackTransaction(); // Undo all if error occurs
            throw e; 
        }
    }

    public Customer getCustomerDetails(int userId) throws SQLException {
        return customerDAO.getCustomerByUserId(userId);
    }
}