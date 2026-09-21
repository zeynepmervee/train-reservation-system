package com.zeynepmervee.trainreservation.trip;

import com.zeynepmervee.trainreservation.station.Station;
import com.zeynepmervee.trainreservation.station.StationNotFoundException;
import com.zeynepmervee.trainreservation.station.StationRepository;
import com.zeynepmervee.trainreservation.train.Train;
import com.zeynepmervee.trainreservation.train.TrainNotFoundException;
import com.zeynepmervee.trainreservation.train.TrainRepository;
import com.zeynepmervee.trainreservation.trip.dto.CreateTripRequest;
import com.zeynepmervee.trainreservation.trip.dto.TripResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

    private static final ZoneId TURKEY_ZONE = ZoneId.of("Europe/Istanbul");

    private final TripRepository tripRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    @Transactional
    public TripResponse create(CreateTripRequest request) {
        validateRequest(request);

        Train train = trainRepository.findById(request.trainId())
                .orElseThrow(() -> new TrainNotFoundException(request.trainId()));

        Station departureStation =
                stationRepository.findById(request.departureStationId())
                        .orElseThrow(() -> new StationNotFoundException(
                                request.departureStationId()
                        ));

        Station arrivalStation =
                stationRepository.findById(request.arrivalStationId())
                        .orElseThrow(() -> new StationNotFoundException(
                                request.arrivalStationId()
                        ));

        if (tripRepository.existsByTrainIdAndDepartureTime(
                request.trainId(),
                request.departureTime()
        )) {
            throw new DuplicateTripException();
        }

        Trip trip = new Trip(
                train,
                departureStation,
                arrivalStation,
                request.departureTime(),
                request.arrivalTime(),
                request.basePrice()
        );

        return TripResponse.from(tripRepository.save(trip));
    }

    public List<TripResponse> search(
            Long departureStationId,
            Long arrivalStationId,
            LocalDate date
    ) {
        Instant startTime = date.atStartOfDay(TURKEY_ZONE).toInstant();
        Instant endTime = date.plusDays(1)
                .atStartOfDay(TURKEY_ZONE)
                .toInstant();

        return tripRepository.search(
                        departureStationId,
                        arrivalStationId,
                        startTime,
                        endTime,
                        TripStatus.SCHEDULED
                )
                .stream()
                .map(TripResponse::from)
                .toList();
    }

    private void validateRequest(CreateTripRequest request) {
        if (request.departureStationId().equals(request.arrivalStationId())) {
            throw new InvalidTripException(
                    "Departure and arrival stations must be different"
            );
        }

        if (!request.arrivalTime().isAfter(request.departureTime())) {
            throw new InvalidTripException(
                    "Arrival time must be after departure time"
            );
        }
    }
}