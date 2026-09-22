package com.zeynepmervee.trainreservation.wagon.dto;

import com.zeynepmervee.trainreservation.reservation.PassengerGender;
import com.zeynepmervee.trainreservation.seat.Seat;

public record SeatResponse(
        Long id,
        String seatNumber,
        boolean window,
        PassengerGender occupancyGender
) {

    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.isWindow(),
                null
        );
    }

    public static SeatResponse from(
            Seat seat,
            PassengerGender occupancyGender
    ) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.isWindow(),
                occupancyGender
        );
    }
}