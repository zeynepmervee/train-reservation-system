package com.zeynepmervee.trainreservation.reservation.dto;

import com.zeynepmervee.trainreservation.reservation.PassengerGender;
import com.zeynepmervee.trainreservation.reservation.PassengerType;
import com.zeynepmervee.trainreservation.reservation.TicketStatus;

import java.math.BigDecimal;

public record TicketResponse(
        Long id,
        Long seatId,
        int wagonNumber,
        String seatNumber,
        String firstName,
        String lastName,
        PassengerGender gender,
        PassengerType passengerType,
        BigDecimal ticketPrice,
        TicketStatus status
) {
}