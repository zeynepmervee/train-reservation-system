package com.zeynepmervee.trainreservation.station;

import com.zeynepmervee.trainreservation.station.dto.CreateStationRequest;
import com.zeynepmervee.trainreservation.station.dto.StationResponse;
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
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StationResponse create(
            @Valid @RequestBody CreateStationRequest request
    ) {
        return stationService.create(request);
    }

    @GetMapping
    public List<StationResponse> findAll() {
        return stationService.findAll();
    }
}