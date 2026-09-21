package com.zeynepmervee.trainreservation.wagon.dto;

import com.zeynepmervee.trainreservation.wagon.WagonType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateWagonRequest(

        @Min(value = 1, message = "Wagon number must be at least 1")
        int wagonNumber,

        @NotNull(message = "Wagon type is required")
        WagonType wagonType,

        @Min(value = 1, message = "Row count must be at least 1")
        @Max(value = 25, message = "Row count cannot exceed 25")
        int rowCount
) {
}