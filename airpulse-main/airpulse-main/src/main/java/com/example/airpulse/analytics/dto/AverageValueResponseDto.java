package com.example.airpulse.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageValueResponseDto {
    private String sensorType;
    private String regionName;
    private Double averageValue;
} 