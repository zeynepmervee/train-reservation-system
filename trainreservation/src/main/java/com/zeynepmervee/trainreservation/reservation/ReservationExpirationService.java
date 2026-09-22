package com.zeynepmervee.trainreservation.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationExpirationService {

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void expireReservations() {
        List<Reservation> expiredReservations =
                reservationRepository.findAllByStatusAndExpiresAtBefore(
                        ReservationStatus.PENDING,
                        Instant.now()
                );

        for (Reservation reservation : expiredReservations) {
            reservation.expire();

            List<Ticket> tickets =
                    ticketRepository.findAllByReservationIdOrderByIdAsc(
                            reservation.getId()
                    );

            for (Ticket ticket : tickets) {
                if (ticket.getStatus() == TicketStatus.HELD) {
                    ticket.expire();
                }
            }
        }
    }
}