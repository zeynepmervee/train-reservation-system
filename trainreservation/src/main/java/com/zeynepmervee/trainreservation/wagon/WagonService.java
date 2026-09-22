package com.zeynepmervee.trainreservation.wagon;

import com.zeynepmervee.trainreservation.seat.Seat;
import com.zeynepmervee.trainreservation.seat.SeatRepository;
import com.zeynepmervee.trainreservation.train.Train;
import com.zeynepmervee.trainreservation.train.TrainNotFoundException;
import com.zeynepmervee.trainreservation.train.TrainRepository;
import com.zeynepmervee.trainreservation.wagon.dto.CreateWagonRequest;
import com.zeynepmervee.trainreservation.wagon.dto.WagonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zeynepmervee.trainreservation.reservation.PassengerGender;
import com.zeynepmervee.trainreservation.reservation.Ticket;
import com.zeynepmervee.trainreservation.reservation.TicketRepository;
import com.zeynepmervee.trainreservation.reservation.TicketStatus;
import com.zeynepmervee.trainreservation.trip.Trip;
import com.zeynepmervee.trainreservation.trip.TripNotFoundException;
import com.zeynepmervee.trainreservation.trip.TripRepository;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WagonService {

    private final WagonRepository wagonRepository;
    private final SeatRepository seatRepository;
    private final TrainRepository trainRepository;
    private final TripRepository tripRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public WagonResponse create(Long trainId, CreateWagonRequest request) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new TrainNotFoundException(trainId));

        if (wagonRepository.existsByTrainIdAndWagonNumber(
                trainId,
                request.wagonNumber()
        )) {
            throw new DuplicateWagonException(request.wagonNumber());
        }

        Wagon wagon = new Wagon(
                train,
                request.wagonNumber(),
                request.wagonType()
        );

        Wagon savedWagon = wagonRepository.save(wagon);
        List<Seat> seats = generateSeats(savedWagon, request.rowCount());
        List<Seat> savedSeats = seatRepository.saveAll(seats);

        return WagonResponse.from(savedWagon, savedSeats);
    }

    public List<WagonResponse> findAllByTrainId(Long trainId) {
        if (!trainRepository.existsById(trainId)) {
            throw new TrainNotFoundException(trainId);
        }

        return wagonRepository.findAllByTrainIdOrderByWagonNumberAsc(trainId)
                .stream()
                .map(wagon -> WagonResponse.from(
                        wagon,
                        seatRepository.findAllByWagonIdOrderByIdAsc(wagon.getId())
                ))
                .toList();
    }

    private List<Seat> generateSeats(Wagon wagon, int rowCount) {
        String[] letters = wagon.getWagonType() == WagonType.ECONOMY
                ? new String[]{"A", "B", "C", "D"}
                : new String[]{"A", "B", "C"};

        List<Seat> seats = new ArrayList<>();

        for (int row = 1; row <= rowCount; row++) {
            for (int index = 0; index < letters.length; index++) {
                String seatNumber = row + letters[index];
                boolean window = index == 0 || index == letters.length - 1;

                seats.add(new Seat(wagon, seatNumber, window));
            }
        }

        return seats;
    }
    public List<WagonResponse> findAllByTripId(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Map<Long, PassengerGender> occupiedSeats =
                ticketRepository.findActiveTickets(
                                tripId,
                                TicketStatus.HELD,
                                TicketStatus.CONFIRMED,
                                Instant.now()
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                ticket -> ticket.getSeat().getId(),
                                Ticket::getGender,
                                (first, second) -> first
                        ));

        return wagonRepository
                .findAllByTrainIdOrderByWagonNumberAsc(
                        trip.getTrain().getId()
                )
                .stream()
                .map(wagon -> WagonResponse.from(
                        wagon,
                        seatRepository.findAllByWagonIdOrderByIdAsc(
                                wagon.getId()
                        ),
                        occupiedSeats
                ))
                .toList();
    }
}