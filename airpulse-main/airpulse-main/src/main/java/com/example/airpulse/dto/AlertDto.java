package com.example.airpulse.dto;

import com.example.airpulse.model.SensorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {
    private UUID sensorId;
    private Double value;
    private SensorType type;
    private String locationCode;
    private String severity; // e.g., GOOD, MODERATE, UNHEALTHY
    private LocalDateTime timestamp;
    private String city;
    private String region;
} 