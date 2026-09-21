package com.zeynepmervee.trainreservation.train;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {

    boolean existsByCodeIgnoreCase(String code);

    List<Train> findAllByOrderByCodeAsc();
}