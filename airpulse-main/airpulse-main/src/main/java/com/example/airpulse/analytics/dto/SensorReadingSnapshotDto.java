package com.example.airpulse.analytics.dto;

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
public class SensorReadingSnapshotDto {
    private UUID sensorId;
    private SensorType sensorType;
    private Double value;
    private LocalDateTime timestamp;
} 