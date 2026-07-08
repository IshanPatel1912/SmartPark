package com.smartpark.services;

import com.smartpark.dao.UserDAO;
import com.smartpark.exceptions.InvalidLoginException;
import com.smartpark.models.User;

import java.sql.SQLException;
import java.util.HashMap;

public class AuthenticationService {

    private UserDAO userDAO;
    private static HashMap<Integer, User> activeSessions = new HashMap<>();

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) throws InvalidLoginException, SQLException {
        User user = userDAO.authenticate(username, password);
        activeSessions.put(user.getId(), user);
        return user;
    }

    public void logout(int userId) {
        activeSessions.remove(userId);
    }

    public boolean isUserLoggedIn(int userId) {
        return activeSessions.containsKey(userId);
    }
}