package com.zeynepmervee.trainreservation.reservation;

import com.zeynepmervee.trainreservation.reservation.dto.CreateReservationRequest;
import com.zeynepmervee.trainreservation.reservation.dto.ReservationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(
            @Valid @RequestBody CreateReservationRequest request
    ) {
        return reservationService.create(request);
    }
}