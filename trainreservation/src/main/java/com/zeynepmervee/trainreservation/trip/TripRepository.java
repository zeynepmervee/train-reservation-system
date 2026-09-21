package com.zeynepmervee.trainreservation.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    boolean existsByTrainIdAndDepartureTime(
            Long trainId,
            Instant departureTime
    );

    @Query("""
            SELECT trip
            FROM Trip trip
            WHERE trip.departureStation.id = :departureStationId
              AND trip.arrivalStation.id = :arrivalStationId
              AND trip.departureTime >= :startTime
              AND trip.departureTime < :endTime
              AND trip.status = :status
            ORDER BY trip.departureTime ASC
            """)
    List<Trip> search(
            @Param("departureStationId") Long departureStationId,
            @Param("arrivalStationId") Long arrivalStationId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("status") TripStatus status
    );
}