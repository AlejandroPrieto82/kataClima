package eci.edu.kata.service;

import eci.edu.kata.dto.WeatherDtos.*;
import eci.edu.kata.model.WeatherData;
import eci.edu.kata.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherResponse getWeather(String locationId) {
        WeatherData data = weatherRepository.findByLocationId(locationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ubicación no encontrada: " + locationId));

        return WeatherResponse.builder()
                .location(LocationDto.builder()
                        .city(data.getCity())
                        .country(data.getCountry())
                        .region(data.getRegion())
                        .build())
                .weather(WeatherDto.builder()
                        .temp(data.getTemp())
                        .pressure(data.getPressure())
                        .humidity(data.getHumidity())
                        .build())
                .build();
    }

    public WeatherResponse reportWeather(String locationId, WeatherRequest request) {
        WeatherData data = weatherRepository.findByLocationId(locationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ubicación no encontrada: " + locationId));

        data.setTemp(request.getWeather().getTemp());
        data.setPressure(request.getWeather().getPressure());
        data.setHumidity(request.getWeather().getHumidity());
        data.setLastUpdated(LocalDateTime.now());
        weatherRepository.save(data);

        return getWeather(locationId);
    }
}