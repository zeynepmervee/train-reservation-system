package com.zeynepmervee.trainreservation.station.dto;

import com.zeynepmervee.trainreservation.station.Station;

import java.time.Instant;

public record StationResponse(
        Long id,
        String name,
        String cityName,
        Instant createdAt
) {

    public static StationResponse from(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCityName(),
                station.getCreatedAt()
        );
    }
}