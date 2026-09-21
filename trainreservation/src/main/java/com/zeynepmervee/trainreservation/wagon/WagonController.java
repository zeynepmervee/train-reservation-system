package com.zeynepmervee.trainreservation.wagon;

import com.zeynepmervee.trainreservation.wagon.dto.CreateWagonRequest;
import com.zeynepmervee.trainreservation.wagon.dto.WagonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trains/{trainId}/wagons")
@RequiredArgsConstructor
public class WagonController {

    private final WagonService wagonService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WagonResponse create(
            @PathVariable Long trainId,
            @Valid @RequestBody CreateWagonRequest request
    ) {
        return wagonService.create(trainId, request);
    }

    @GetMapping
    public List<WagonResponse> findAll(@PathVariable Long trainId) {
        return wagonService.findAllByTrainId(trainId);
    }
}