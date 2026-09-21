package com.zeynepmervee.trainreservation.train;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateTrainException extends RuntimeException {

    public DuplicateTrainException(String message) {
        super(message);
    }
}