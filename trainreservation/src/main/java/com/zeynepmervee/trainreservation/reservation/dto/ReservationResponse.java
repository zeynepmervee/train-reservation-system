package com.zeynepmervee.trainreservation.reservation.dto;

import com.zeynepmervee.trainreservation.reservation.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReservationResponse(
        Long id,
        Long tripId,
        ReservationStatus status,
        BigDecimal totalPrice,
        Instant expiresAt,
        List<TicketResponse> tickets
) {
}