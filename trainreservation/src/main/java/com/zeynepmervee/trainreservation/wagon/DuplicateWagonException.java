package com.zeynepmervee.trainreservation.wagon;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateWagonException extends RuntimeException {

    public DuplicateWagonException(int wagonNumber) {
        super("Wagon already exists with number: " + wagonNumber);
    }
}