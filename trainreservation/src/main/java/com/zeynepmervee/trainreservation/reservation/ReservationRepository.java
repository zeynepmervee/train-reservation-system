package com.zeynepmervee.trainreservation.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByStatusAndExpiresAtBefore(
            ReservationStatus status,
            Instant time
    );
}