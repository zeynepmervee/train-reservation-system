package com.zeynepmervee.trainreservation.train;

import com.zeynepmervee.trainreservation.train.dto.CreateTrainRequest;
import com.zeynepmervee.trainreservation.train.dto.TrainResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainService {

    private final TrainRepository trainRepository;

    @Transactional
    public TrainResponse create(CreateTrainRequest request) {
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        String name = request.name().trim();

        if (trainRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateTrainException(
                    "Train already exists with code: " + code
            );
        }

        Train train = new Train(code, name);
        Train savedTrain = trainRepository.save(train);

        return TrainResponse.from(savedTrain);
    }

    public List<TrainResponse> findAll() {
        return trainRepository.findAllByOrderByCodeAsc()
                .stream()
                .map(TrainResponse::from)
                .toList();
    }
}