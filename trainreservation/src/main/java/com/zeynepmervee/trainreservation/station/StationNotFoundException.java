package com.zeynepmervee.trainreservation.station;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException(Long id) {
        super("Station not found with id: " + id);
    }
}