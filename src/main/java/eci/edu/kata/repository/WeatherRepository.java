package eci.edu.kata.repository;

import eci.edu.kata.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findByLocationId(String locationId);
}