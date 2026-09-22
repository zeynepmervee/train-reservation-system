package com.zeynepmervee.trainreservation.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByReservationIdOrderByIdAsc(Long reservationId);

    List<Ticket> findAllByTripIdAndStatusIn(
            Long tripId,
            Collection<TicketStatus> statuses
    );

    @Query("""
            SELECT ticket
            FROM Ticket ticket
            WHERE ticket.trip.id = :tripId
              AND (
                    ticket.status = :confirmedStatus
                    OR (
                        ticket.status = :heldStatus
                        AND ticket.reservation.expiresAt > :now
                    )
              )
            """)
    List<Ticket> findActiveTickets(
            @Param("tripId") Long tripId,
            @Param("heldStatus") TicketStatus heldStatus,
            @Param("confirmedStatus") TicketStatus confirmedStatus,
            @Param("now") Instant now
    );
}