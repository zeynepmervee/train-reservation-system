package com.zeynepmervee.trainreservation.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByReservationIdOrderByIdAsc(Long reservationId);

    List<Ticket> findAllByTripIdAndStatusIn(
            Long tripId,
            Collection<TicketStatus> statuses
    );
}