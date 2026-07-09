package com.smartpark.services;

import com.smartpark.dao.PricingDAO;
import com.smartpark.models.Pricing;

import java.math.BigDecimal;
import java.sql.SQLException;

public class PricingService {

    private PricingDAO pricingDAO;

    public PricingService() {
        this.pricingDAO = new PricingDAO();
    }

    // SIGNATURE UPDATED FOR BIGDECIMAL
    public void setPricing(String vehicleType, BigDecimal baseRate, BigDecimal hourlyRate) throws SQLException {
        Pricing pricing = new Pricing(0, vehicleType, baseRate, hourlyRate);
        pricingDAO.addOrUpdatePricing(pricing);
    }

    public Pricing getPricingDetails(String vehicleType) throws SQLException {
        return pricingDAO.getPricingByVehicleType(vehicleType);
    }
}