package com.zeynepmervee.trainreservation.reservation;

import com.zeynepmervee.trainreservation.reservation.dto.CreateReservationRequest;
import com.zeynepmervee.trainreservation.reservation.dto.PassengerRequest;
import com.zeynepmervee.trainreservation.reservation.dto.ReservationResponse;
import com.zeynepmervee.trainreservation.reservation.dto.TicketResponse;
import com.zeynepmervee.trainreservation.seat.Seat;
import com.zeynepmervee.trainreservation.seat.SeatNotFoundException;
import com.zeynepmervee.trainreservation.seat.SeatRepository;
import com.zeynepmervee.trainreservation.trip.Trip;
import com.zeynepmervee.trainreservation.trip.TripNotFoundException;
import com.zeynepmervee.trainreservation.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public ReservationResponse create(CreateReservationRequest request) {
        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new TripNotFoundException(request.tripId()));

        validateDistinctSeats(request.passengers());

        Map<Long, Seat> seatsById = findAndValidateSeats(
                trip,
                request.passengers()
        );

        BigDecimal totalPrice = request.passengers()
                .stream()
                .map(passenger -> calculatePrice(
                        trip.getBasePrice(),
                        passenger.passengerType()
                ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Reservation reservation = new Reservation(
                trip,
                totalPrice,
                Instant.now().plus(Duration.ofMinutes(5))
        );

        Reservation savedReservation =
                reservationRepository.save(reservation);

        List<Ticket> tickets = new ArrayList<>();

        for (PassengerRequest passenger : request.passengers()) {
            Seat seat = seatsById.get(passenger.seatId());
            BigDecimal ticketPrice = calculatePrice(
                    trip.getBasePrice(),
                    passenger.passengerType()
            );

            tickets.add(new Ticket(
                    savedReservation,
                    trip,
                    seat,
                    passenger.firstName().trim(),
                    passenger.lastName().trim(),
                    passenger.birthDate(),
                    passenger.gender(),
                    passenger.passengerType(),
                    ticketPrice
            ));
        }

        try {
            List<Ticket> savedTickets =
                    ticketRepository.saveAllAndFlush(tickets);

            return toResponse(savedReservation, savedTickets);
        } catch (DataIntegrityViolationException exception) {
            throw new SeatUnavailableException();
        }
    }

    private void validateDistinctSeats(List<PassengerRequest> passengers) {
        Set<Long> seatIds = new HashSet<>();

        for (PassengerRequest passenger : passengers) {
            if (!seatIds.add(passenger.seatId())) {
                throw new InvalidReservationException(
                        "The same seat cannot be selected more than once"
                );
            }
        }
    }

    private Map<Long, Seat> findAndValidateSeats(
            Trip trip,
            List<PassengerRequest> passengers
    ) {
        List<Long> seatIds = passengers.stream()
                .map(PassengerRequest::seatId)
                .toList();

        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            Set<Long> foundIds = new HashSet<>();

            for (Seat seat : seats) {
                foundIds.add(seat.getId());
            }

            Long missingId = seatIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElseThrow();

            throw new SeatNotFoundException(missingId);
        }

        Map<Long, Seat> seatsById = new HashMap<>();

        for (Seat seat : seats) {
            Long seatTrainId = seat.getWagon().getTrain().getId();
            Long tripTrainId = trip.getTrain().getId();

            if (!seatTrainId.equals(tripTrainId)) {
                throw new InvalidReservationException(
                        "Selected seat does not belong to the trip train"
                );
            }

            seatsById.put(seat.getId(), seat);
        }

        return seatsById;
    }

    private BigDecimal calculatePrice(
            BigDecimal basePrice,
            PassengerType passengerType
    ) {
        BigDecimal multiplier = switch (passengerType) {
            case STANDARD -> BigDecimal.ONE;
            case STUDENT -> new BigDecimal("0.80");
            case SENIOR -> new BigDecimal("0.50");
        };

        return basePrice.multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private ReservationResponse toResponse(
            Reservation reservation,
            List<Ticket> tickets
    ) {
        List<TicketResponse> ticketResponses = tickets.stream()
                .map(ticket -> new TicketResponse(
                        ticket.getId(),
                        ticket.getSeat().getId(),
                        ticket.getSeat().getWagon().getWagonNumber(),
                        ticket.getSeat().getSeatNumber(),
                        ticket.getFirstName(),
                        ticket.getLastName(),
                        ticket.getGender(),
                        ticket.getPassengerType(),
                        ticket.getTicketPrice(),
                        ticket.getStatus()
                ))
                .toList();

        return new ReservationResponse(
                reservation.getId(),
                reservation.getTrip().getId(),
                reservation.getStatus(),
                reservation.getTotalPrice(),
                reservation.getExpiresAt(),
                ticketResponses
        );
    }
}