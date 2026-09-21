package com.zeynepmervee.trainreservation.train;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TrainNotFoundException extends RuntimeException {

    public TrainNotFoundException(Long id) {
        super("Train not found with id: " + id);
    }
}