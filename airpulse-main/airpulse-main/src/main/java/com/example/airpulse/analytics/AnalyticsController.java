package com.example.airpulse.analytics;

import com.example.airpulse.analytics.dto.AlertsCountResponseDto;
import com.example.airpulse.analytics.dto.AverageValueResponseDto;
import com.example.airpulse.analytics.dto.RegionAnalyticsDto;
import com.example.airpulse.model.SensorType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/average")
    public ResponseEntity<AverageValueResponseDto> getAverageReading(
            @RequestParam SensorType type,
            @RequestParam String region,
            @RequestParam(defaultValue = "10") long minutes) {
        Optional<AverageValueResponseDto> averageDto = analyticsService.getAverageReading(type, region, minutes);
        return averageDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/alerts/count")
    public ResponseEntity<AlertsCountResponseDto> getAlertsCount(
            @RequestParam String severity,
            @RequestParam(defaultValue = "60") long minutes) {
        AlertsCountResponseDto countDto = analyticsService.getAlertsCount(severity, minutes);
        return ResponseEntity.ok(countDto);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, RegionAnalyticsDto>> getCitySummary(@RequestParam String city) {
        Optional<Map<String, RegionAnalyticsDto>> summaryOpt = analyticsService.getCitySummary(city);
        return summaryOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Optional: Endpoint to manually clear caches if needed for admin purposes
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearAllCaches() {
        analyticsService.clearAllCaches();
        return ResponseEntity.ok("All analytics caches cleared.");
    }
} 