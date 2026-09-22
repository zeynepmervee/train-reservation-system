package com.zeynepmervee.trainreservation.reservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateReservationRequest(

        @NotNull(message = "Trip id is required")
        Long tripId,

        @NotEmpty(message = "At least one passenger is required")
        @Size(max = 10, message = "At most 10 passengers can be added")
        List<@Valid PassengerRequest> passengers
) {
}