package com.zeynepmervee.trainreservation.wagon.dto;

import com.zeynepmervee.trainreservation.seat.Seat;
import com.zeynepmervee.trainreservation.wagon.Wagon;
import com.zeynepmervee.trainreservation.wagon.WagonType;

import java.util.List;

public record WagonResponse(
        Long id,
        Long trainId,
        int wagonNumber,
        WagonType wagonType,
        List<SeatResponse> seats
) {

    public static WagonResponse from(Wagon wagon, List<Seat> seats) {
        return new WagonResponse(
                wagon.getId(),
                wagon.getTrain().getId(),
                wagon.getWagonNumber(),
                wagon.getWagonType(),
                seats.stream().map(SeatResponse::from).toList()
        );
    }
}