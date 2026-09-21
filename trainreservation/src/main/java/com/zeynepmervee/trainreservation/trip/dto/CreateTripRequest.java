package com.zeynepmervee.trainreservation.trip.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTripRequest(

        @NotNull(message = "Train id is required")
        Long trainId,

        @NotNull(message = "Departure station id is required")
        Long departureStationId,

        @NotNull(message = "Arrival station id is required")
        Long arrivalStationId,

        @NotNull(message = "Departure time is required")
        Instant departureTime,

        @NotNull(message = "Arrival time is required")
        Instant arrivalTime,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = false,
                message = "Base price must be greater than zero")
        BigDecimal basePrice
) {
}