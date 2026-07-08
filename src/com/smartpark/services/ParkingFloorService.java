package com.smartpark.services;

import com.smartpark.dao.ParkingFloorDAO;
import com.smartpark.models.ParkingFloor;

import java.sql.SQLException;
import java.util.List;

public class ParkingFloorService {

    private ParkingFloorDAO parkingFloorDAO;

    public ParkingFloorService() {
        this.parkingFloorDAO = new ParkingFloorDAO();
    }

    public int addFloor(int floorNumber, int totalCapacity) throws SQLException {
        ParkingFloor floor = new ParkingFloor(0, floorNumber, totalCapacity);
        return parkingFloorDAO.addFloor(floor);
    }

    public List<ParkingFloor> getAllFloors() throws SQLException {
        return parkingFloorDAO.getAllFloors();
    }
}