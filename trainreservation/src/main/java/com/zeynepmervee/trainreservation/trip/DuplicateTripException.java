package com.zeynepmervee.trainreservation.trip;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateTripException extends RuntimeException {

    public DuplicateTripException() {
        super("This train already has a trip at the specified departure time");
    }
}