package com.smartpark.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DBConnection {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "smartpark_db";
    private static final String PARAMS = "?allowMultiQueries=true&createDatabaseIfNotExist=true";
    
    private static String user = "root";
    private static String password = "Ishan1912";
    private static Connection connection = null;

    private DBConnection() {}

    public static String getDbUser() {
        return user;
    }

    public static String getDbPassword() {
        if (password == null) {
            detectPassword();
        }
        return password;
    }

    private static void detectPassword() {
        String[] trialPasswords = new String[]{"root", "", "admin", "1234", "123456", "password"};
        
        for (String trial : trialPasswords) {
            try (Connection testConn = DriverManager.getConnection(BASE_URL + PARAMS, user, trial)) {
                password = trial;
                return;
            } catch (SQLException ignored) {}
        }

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n[!] Automatic password detection failed.");
        System.out.print("Please enter your local MySQL 'root' user password: ");
        password = scanner.nextLine();
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String fullUrl = BASE_URL + DB_NAME + PARAMS;
                connection = DriverManager.getConnection(fullUrl, user, getDbPassword());
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}