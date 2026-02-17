package eci.edu.kata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

public class WeatherDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherResponse {
        private LocationDto location;
        private WeatherDto weather;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        private String city;
        private String country;
        private String region;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherDto {
        private Double temp;
        private Double pressure;
        private Integer humidity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherRequest {
        @NotNull
        @Valid
        private WeatherDto weather;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthRequest {
        private String username;
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
    }
}