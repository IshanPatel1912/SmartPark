package com.smartpark.services;

import com.smartpark.dao.VehicleDAO;
import com.smartpark.exceptions.DuplicateEntityException;
import com.smartpark.models.Vehicle;

import java.sql.SQLException;
import java.util.List;

public class VehicleService {

    private VehicleDAO vehicleDAO;

    public VehicleService() {
        this.vehicleDAO = new VehicleDAO();
    }

    public void registerVehicle(int customerId, String licensePlate, String vehicleType) throws DuplicateEntityException, SQLException {
        Vehicle vehicle = new Vehicle(0, customerId, licensePlate, vehicleType);
        vehicleDAO.addVehicle(vehicle);
    }

    public Vehicle getVehicleByLicensePlate(String licensePlate) throws SQLException {
        return vehicleDAO.getVehicleByLicensePlate(licensePlate);
    }

    public List<Vehicle> getCustomerVehicles(int customerId) throws SQLException {
        return vehicleDAO.getVehiclesByCustomerId(customerId);
    }
}