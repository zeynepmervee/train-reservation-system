package com.zeynepmervee.trainreservation.wagon.dto;

import com.zeynepmervee.trainreservation.seat.Seat;

public record SeatResponse(
        Long id,
        String seatNumber,
        boolean window
) {

    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.isWindow()
        );
    }
}