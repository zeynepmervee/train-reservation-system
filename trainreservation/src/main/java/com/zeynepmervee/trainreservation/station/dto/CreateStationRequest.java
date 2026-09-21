package com.zeynepmervee.trainreservation.station.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStationRequest(

        @NotBlank(message = "Station name cannot be blank")
        @Size(max = 100, message = "Station name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "City name cannot be blank")
        @Size(max = 100, message = "City name cannot exceed 100 characters")
        String cityName
) {
}