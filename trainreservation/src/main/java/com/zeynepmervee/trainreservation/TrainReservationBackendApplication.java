package com.zeynepmervee.trainreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrainReservationBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainReservationBackendApplication.class, args);
	}

}
