package com.zeynepmervee.trainreservation.wagon.dto;

import com.zeynepmervee.trainreservation.reservation.PassengerGender;
import com.zeynepmervee.trainreservation.seat.Seat;
import com.zeynepmervee.trainreservation.wagon.Wagon;
import com.zeynepmervee.trainreservation.wagon.WagonType;

import java.util.List;
import java.util.Map;

public record WagonResponse(
        Long id,
        Long trainId,
        int wagonNumber,
        WagonType wagonType,
        List<SeatResponse> seats
) {

    public static WagonResponse from(Wagon wagon, List<Seat> seats) {
        return from(wagon, seats, Map.of());
    }

    public static WagonResponse from(
            Wagon wagon,
            List<Seat> seats,
            Map<Long, PassengerGender> occupiedSeats
    ) {
        return new WagonResponse(
                wagon.getId(),
                wagon.getTrain().getId(),
                wagon.getWagonNumber(),
                wagon.getWagonType(),
                seats.stream()
                        .map(seat -> SeatResponse.from(
                                seat,
                                occupiedSeats.get(seat.getId())
                        ))
                        .toList()
        );
    }
}