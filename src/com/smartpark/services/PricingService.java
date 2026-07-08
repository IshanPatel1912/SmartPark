package com.smartpark.services;

import com.smartpark.dao.PricingDAO;
import com.smartpark.models.Pricing;

import java.sql.SQLException;

public class PricingService {

    private PricingDAO pricingDAO;

    public PricingService() {
        this.pricingDAO = new PricingDAO();
    }

    public void setPricing(String vehicleType, double baseRate, double hourlyRate) throws SQLException {
        Pricing pricing = new Pricing(0, vehicleType, baseRate, hourlyRate);
        pricingDAO.addOrUpdatePricing(pricing);
    }

    public Pricing getPricingDetails(String vehicleType) throws SQLException {
        return pricingDAO.getPricingByVehicleType(vehicleType);
    }
}