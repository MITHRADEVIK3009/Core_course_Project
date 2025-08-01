package com.example.airpulse.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionAnalyticsDto {
    private String regionName;
    private String currentAqiSeverity;
    private SensorReadingSnapshotDto latestReading;
} 