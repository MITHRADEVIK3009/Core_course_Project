package com.example.airpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AirpulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirpulseApplication.class, args);
    }

} 