package com.zeynepmervee.trainreservation.train.dto;

import com.zeynepmervee.trainreservation.train.Train;

import java.time.Instant;

public record TrainResponse(
        Long id,
        String code,
        String name,
        boolean active,
        Instant createdAt
) {

    public static TrainResponse from(Train train) {
        return new TrainResponse(
                train.getId(),
                train.getCode(),
                train.getName(),
                train.isActive(),
                train.getCreatedAt()
        );
    }
}