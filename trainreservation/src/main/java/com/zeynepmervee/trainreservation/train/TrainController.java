package com.zeynepmervee.trainreservation.train;

import com.zeynepmervee.trainreservation.train.dto.CreateTrainRequest;
import com.zeynepmervee.trainreservation.train.dto.TrainResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainResponse create(
            @Valid @RequestBody CreateTrainRequest request
    ) {
        return trainService.create(request);
    }

    @GetMapping
    public List<TrainResponse> findAll() {
        return trainService.findAll();
    }
}