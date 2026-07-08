package com.smartpark.interfaces;

public interface Payment {
    boolean processPayment(double amount);
    String getPaymentStatus();
}