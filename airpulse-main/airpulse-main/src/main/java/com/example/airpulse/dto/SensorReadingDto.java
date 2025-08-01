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
public class SensorReadingDto {
    private UUID sensorId;
    private LocalDateTime timestamp;
    private Double value;
    private String unit;
    private SensorType type;
    private String locationCode; // From Location's codePrefix
    private String city;
    private String region;
} 