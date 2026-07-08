package com.smartpark.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileHandler {

    private static final String LOG_DIR = "logs/";
    private static final String RECEIPT_DIR = "data/receipts/";
    private static final String BACKUP_DIR = "data/backups/";

    public static void initializeDirectories() {
        new File(LOG_DIR).mkdirs();
        new File(RECEIPT_DIR).mkdirs();
        new File(BACKUP_DIR).mkdirs();
    }

    public static void writeLog(String level, String message) throws IOException {
        initializeDirectories();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("[%s] [%s] %s\n", timestamp, level, message);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_DIR + "system_logs.txt", true))) {
            writer.write(logEntry);
        }
    }

    public static void saveReceipt(String fileName, String content) throws IOException {
        initializeDirectories();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECEIPT_DIR + fileName))) {
            writer.write(content);
        }
    }

    public static void saveReport(String fileName, String content) throws IOException {
        initializeDirectories();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BACKUP_DIR + fileName))) {
            writer.write(content);
        }
    }
}