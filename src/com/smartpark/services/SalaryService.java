package com.smartpark.services;

import com.smartpark.dao.SalaryDAO;
import com.smartpark.models.SalaryDisbursement;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SalaryService {

    private SalaryDAO salaryDAO;

    public SalaryService() {
        this.salaryDAO = new SalaryDAO();
    }

    // SIGNATURE UPDATED FOR BIGDECIMAL
    public void payEmployee(int employeeId, BigDecimal amount, LocalDate paymentDate) throws SQLException {
        salaryDAO.recordSalaryPayment(employeeId, amount, paymentDate);
    }

    public List<SalaryDisbursement> getSalaryReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        return salaryDAO.getDisbursementsBetweenDates(startDate, endDate);
    }
}