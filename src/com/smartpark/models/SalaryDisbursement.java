package com.smartpark.models;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SalaryDisbursement {
    private int id;
    private int employeeId;
    private BigDecimal amount;
    private LocalDate paymentDate;

    public SalaryDisbursement(int id, int employeeId, BigDecimal amount, LocalDate paymentDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
}