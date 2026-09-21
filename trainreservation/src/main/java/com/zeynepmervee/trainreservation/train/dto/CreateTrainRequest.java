package com.zeynepmervee.trainreservation.train.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTrainRequest(

        @NotBlank(message = "Train code cannot be blank")
        @Size(max = 30, message = "Train code cannot exceed 30 characters")
        String code,

        @NotBlank(message = "Train name cannot be blank")
        @Size(max = 100, message = "Train name cannot exceed 100 characters")
        String name
) {
}