package com.zeynepmervee.trainreservation.seat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeatNotFoundException extends RuntimeException {

    public SeatNotFoundException(Long id) {
        super("Seat not found with id: " + id);
    }
}