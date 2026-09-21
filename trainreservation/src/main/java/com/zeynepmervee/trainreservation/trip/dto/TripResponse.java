package com.zeynepmervee.trainreservation.trip.dto;

import com.zeynepmervee.trainreservation.trip.Trip;
import com.zeynepmervee.trainreservation.trip.TripStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TripResponse(
        Long id,
        Long trainId,
        String trainCode,
        Long departureStationId,
        String departureStationName,
        Long arrivalStationId,
        String arrivalStationName,
        Instant departureTime,
        Instant arrivalTime,
        BigDecimal basePrice,
        TripStatus status
) {

    public static TripResponse from(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getTrain().getId(),
                trip.getTrain().getCode(),
                trip.getDepartureStation().getId(),
                trip.getDepartureStation().getName(),
                trip.getArrivalStation().getId(),
                trip.getArrivalStation().getName(),
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getBasePrice(),
                trip.getStatus()
        );
    }
}