package com.smartpark.models;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private int id;
    private int sessionId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime billingTime;

    public Bill(int id, int sessionId, BigDecimal amount, String paymentStatus, LocalDateTime billingTime) {
        this.id = id;
        this.sessionId = sessionId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.billingTime = billingTime;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getBillingTime() { return billingTime; }
    public void setBillingTime(LocalDateTime billingTime) { this.billingTime = billingTime; }
}