package eci.edu.kata.controller;

import eci.edu.kata.dto.WeatherDtos.*;
import eci.edu.kata.service.WeatherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/{locationId}")
    public ResponseEntity<WeatherResponse> getWeather(@PathVariable String locationId) {
        return ResponseEntity.ok(weatherService.getWeather(locationId));
    }

    @PostMapping("/{locationId}")
    public ResponseEntity<WeatherResponse> reportWeather(
            @PathVariable String locationId,
            @Valid @RequestBody WeatherRequest request) {
        return ResponseEntity.ok(weatherService.reportWeather(locationId, request));
    }
}