package com.zeynepmervee.trainreservation.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeatUnavailableException extends RuntimeException {

    public SeatUnavailableException() {
        super("One or more selected seats are no longer available");
    }
}