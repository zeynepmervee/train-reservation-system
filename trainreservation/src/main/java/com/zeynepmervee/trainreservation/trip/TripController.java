package com.zeynepmervee.trainreservation.trip;

import com.zeynepmervee.trainreservation.trip.dto.CreateTripRequest;
import com.zeynepmervee.trainreservation.trip.dto.TripResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse create(
            @Valid @RequestBody CreateTripRequest request
    ) {
        return tripService.create(request);
    }

    @GetMapping("/search")
    public List<TripResponse> search(
            @RequestParam Long departureStationId,
            @RequestParam Long arrivalStationId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return tripService.search(
                departureStationId,
                arrivalStationId,
                date
        );
    }
    @GetMapping("/{id}")
    public TripResponse findById(@PathVariable Long id) {
        return tripService.findById(id);
    }
}