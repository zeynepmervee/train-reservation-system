package com.zeynepmervee.trainreservation.station;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

    boolean existsByNameIgnoreCaseAndCityNameIgnoreCase(
            String name,
            String cityName
    );

    List<Station> findAllByOrderByCityNameAscNameAsc();
}