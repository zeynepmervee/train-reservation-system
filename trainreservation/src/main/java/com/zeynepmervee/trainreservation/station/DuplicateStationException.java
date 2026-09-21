package com.zeynepmervee.trainreservation.station;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateStationException extends RuntimeException {

    public DuplicateStationException(String message) {
        super(message);
    }
}