package com.zeynepmervee.trainreservation.wagon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WagonRepository extends JpaRepository<Wagon, Long> {

    boolean existsByTrainIdAndWagonNumber(Long trainId, int wagonNumber);

    List<Wagon> findAllByTrainIdOrderByWagonNumberAsc(Long trainId);
}