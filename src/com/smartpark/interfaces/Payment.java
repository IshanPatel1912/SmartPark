package com.smartpark.interfaces;

import java.sql.SQLException;

public interface Payment {
    void processPayment(int billId) throws SQLException;
}