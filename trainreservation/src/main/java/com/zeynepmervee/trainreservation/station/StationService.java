package com.zeynepmervee.trainreservation.station;

import com.zeynepmervee.trainreservation.station.dto.CreateStationRequest;
import com.zeynepmervee.trainreservation.station.dto.StationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;

    @Transactional
    public StationResponse create(CreateStationRequest request) {
        String name = request.name().trim();
        String cityName = request.cityName().trim();

        boolean stationExists =
                stationRepository.existsByNameIgnoreCaseAndCityNameIgnoreCase(
                        name,
                        cityName
                );

        if (stationExists) {
            throw new DuplicateStationException(
                    "Station already exists: " + name + ", " + cityName
            );
        }

        Station station = new Station(name, cityName);
        Station savedStation = stationRepository.save(station);

        return StationResponse.from(savedStation);
    }

    public List<StationResponse> findAll() {
        return stationRepository.findAllByOrderByCityNameAscNameAsc()
                .stream()
                .map(StationResponse::from)
                .toList();
    }
}