package com.smartpark.services;

import com.smartpark.dao.ReservationDAO;
import com.smartpark.exceptions.ReservationNotFoundException;
import com.smartpark.exceptions.SlotOccupiedException;
import com.smartpark.models.Reservation;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ReservationService {

    private ReservationDAO reservationDAO;
    private ParkingSlotService slotService;
    private PriorityQueue<Reservation> reservationQueue;

    public ReservationService(ParkingSlotService slotService) {
        this.reservationDAO = new ReservationDAO();
        this.slotService = slotService;
        this.reservationQueue = new PriorityQueue<>(Comparator.comparing(Reservation::getReservationTime));
    }

    public int createReservation(int customerId, int vehicleId, int slotId, int durationInHours) throws SQLException, SlotOccupiedException {
        if (!"AVAILABLE".equalsIgnoreCase(slotService.getSlotDetails(slotId).getStatus())) {
            throw new SlotOccupiedException("Selected slot is currently not available.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(durationInHours);

        Reservation reservation = new Reservation(0, customerId, vehicleId, slotId, now, expiry, "ACTIVE");
        int reservationId = reservationDAO.createReservation(reservation);

        if (reservationId > 0) {
            reservation.setId(reservationId);
            slotService.updateSlotStatus(slotId, "RESERVED");
            reservationQueue.offer(reservation);
        }

        return reservationId;
    }

    public Reservation getReservationDetails(int reservationId) throws SQLException, ReservationNotFoundException {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " does not exist.");
        }
        return reservation;
    }

    public void cancelReservation(int reservationId) throws SQLException, ReservationNotFoundException {
        Reservation reservation = getReservationDetails(reservationId);
        reservationDAO.updateReservationStatus(reservationId, "CANCELLED");
        slotService.updateSlotStatus(reservation.getSlotId(), "AVAILABLE");
        reservationQueue.removeIf(r -> r.getId() == reservationId);
    }
}