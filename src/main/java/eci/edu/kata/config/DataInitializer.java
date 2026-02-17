package eci.edu.kata.config;

import eci.edu.kata.model.WeatherData;
import eci.edu.kata.model.User;
import eci.edu.kata.repository.UserRepository;
import eci.edu.kata.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WeatherRepository weatherRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        userRepository.save(User.builder()
                .username("kata-user")
                .password(passwordEncoder.encode("password123"))
                .build());

        weatherRepository.save(WeatherData.builder()
                .locationId("bogota-col")
                .city("Bogotá")
                .country("Colombia")
                .region("Cundinamarca")
                .temp(17.5)
                .pressure(994.71)
                .humidity(61)
                .lastUpdated(LocalDateTime.now())
                .build());

        log.info("✅ Datos listos — usuario: kata-user / password123");
    }
}