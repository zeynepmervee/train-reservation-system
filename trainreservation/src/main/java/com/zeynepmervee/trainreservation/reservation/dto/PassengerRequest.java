package com.zeynepmervee.trainreservation.reservation.dto;

import com.zeynepmervee.trainreservation.reservation.PassengerGender;
import com.zeynepmervee.trainreservation.reservation.PassengerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PassengerRequest(

        @NotNull(message = "Seat id is required")
        Long seatId,

        @NotBlank(message = "First name cannot be blank")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 100)
        String lastName,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotNull(message = "Gender is required")
        PassengerGender gender,

        @NotNull(message = "Passenger type is required")
        PassengerType passengerType
) {
}