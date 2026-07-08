package com.smartpark.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String BASE_URL = "jdbc:mysql://localhost:3306/?allowMultiQueries=true&createDatabaseIfNotExist=true";
    private static final String SCHEMA_PATH = "db/schema.sql";

    public static void initializeDatabase() {
        String dbUser = DBConnection.getDbUser();
        String dbPassword = DBConnection.getDbPassword();

        try (Connection conn = DriverManager.getConnection(BASE_URL, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {

            String schemaContent = new String(Files.readAllBytes(Paths.get(SCHEMA_PATH)));
            String[] sqlStatements = schemaContent.split(";");

            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
            
            FileHandler.writeLog("INFO", "Database initialized successfully from schema.sql.");
            System.out.println("Database setup complete!");

        } catch (Exception e) {
            System.out.println("Database initialization error: " + e.getMessage());
        }
    }
}