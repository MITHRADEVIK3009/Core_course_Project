package com.example.airpulse.analytics;

import com.example.airpulse.analytics.dto.AlertsCountResponseDto;
import com.example.airpulse.analytics.dto.AverageValueResponseDto;
import com.example.airpulse.analytics.dto.RegionAnalyticsDto;
import com.example.airpulse.analytics.dto.SensorReadingSnapshotDto;
import com.example.airpulse.consumer.AqiEvaluator;
import com.example.airpulse.model.*;
import com.example.airpulse.repository.AlertRepository;
import com.example.airpulse.repository.CityRepository;
import com.example.airpulse.repository.RegionRepository;
import com.example.airpulse.repository.SensorDataRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalyticsService {

    private final SensorDataRepository sensorDataRepository;
    private final AlertRepository alertRepository;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final AqiEvaluator aqiEvaluator;

    public AnalyticsService(SensorDataRepository sensorDataRepository,
                            AlertRepository alertRepository,
                            CityRepository cityRepository,
                            RegionRepository regionRepository,
                            AqiEvaluator aqiEvaluator) {
        this.sensorDataRepository = sensorDataRepository;
        this.alertRepository = alertRepository;
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.aqiEvaluator = aqiEvaluator;
    }

    @Cacheable(value = "averageByTypeAndRegion", key = "{#type.name() + '-' + #region + '-' + #minutes}")
    public Optional<AverageValueResponseDto> getAverageReading(SensorType type, String region, long minutes) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(minutes, ChronoUnit.MINUTES);

        Double average = sensorDataRepository.findAverageValueBySensorTypeAndRegionInTimeRange(
                type, region, startTime, endTime);

        if (average == null) {
            return Optional.empty();
        }

        return Optional.of(AverageValueResponseDto.builder()
                .sensorType(type.name())
                .regionName(region)
                .averageValue(average)
                .build());
    }

    @Cacheable(value = "alertCount", key = "{#severity + '-' + #minutes}")
    public AlertsCountResponseDto getAlertsCount(String severity, long minutes) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(minutes, ChronoUnit.MINUTES);
        Long count = alertRepository.countBySeverityAndTimestampBetween(severity.toUpperCase(), startTime, endTime);
        return AlertsCountResponseDto.builder()
                .severity(severity)
                .count(count != null ? count : 0L)
                .build();
    }

    @Cacheable(value = "summaryByCity", key = "#city")
    public Optional<Map<String, RegionAnalyticsDto>> getCitySummary(String city) {
        Optional<City> cityOptional = cityRepository.findByName(city);
        if (cityOptional.isEmpty()) {
            return Optional.empty();
        }

        List<Region> regions = regionRepository.findByCityId(cityOptional.get().getId());
        Map<String, RegionAnalyticsDto> summary = new HashMap<>();

        for (Region region : regions) {
            Optional<SensorData> latestReadingOpt = sensorDataRepository.findTopBySensorLocationRegionIdOrderByTimestampDesc(region.getId());

            if (latestReadingOpt.isPresent()) {
                SensorData latestReading = latestReadingOpt.get();
                String aqiSeverity = aqiEvaluator.evaluateAqi(latestReading.getType(), latestReading.getValue()).name();

                SensorReadingSnapshotDto snapshot = SensorReadingSnapshotDto.builder()
                        .sensorId(latestReading.getSensorId())
                        .sensorType(latestReading.getType())
                        .value(latestReading.getValue())
                        .timestamp(latestReading.getTimestamp())
                        .build();

                summary.put(region.getName(), RegionAnalyticsDto.builder()
                        .regionName(region.getName())
                        .currentAqiSeverity(aqiSeverity)
                        .latestReading(snapshot)
                        .build());
            } else {
                summary.put(region.getName(), RegionAnalyticsDto.builder()
                        .regionName(region.getName())
                        .currentAqiSeverity("NO_DATA")
                        .latestReading(null)
                        .build());
            }
        }
        return Optional.of(summary);
    }

    @CacheEvict(allEntries = true, value = {
            "averageByTypeAndRegion",
            "alertCount",
            "summaryByCity"
    })
    public void clearAllCaches() {
        // This method, when called, will clear all entries in the specified caches.
        // Intentionally empty as Spring Cache handles the eviction.
    }
} 