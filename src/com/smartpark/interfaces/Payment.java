package com.smartpark.interfaces;

import java.sql.SQLException;

public interface Payment {
    // Forces any implementing class to have a database-linked payment processor
    void processPayment(int billId) throws SQLException;
}