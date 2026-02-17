package eci.edu.kata.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_id", nullable = false, unique = true)
    private String locationId;

    private String city;
    private String country;
    private String region;

    private Double temp;
    private Double pressure;
    private Integer humidity;

    private LocalDateTime lastUpdated;
}