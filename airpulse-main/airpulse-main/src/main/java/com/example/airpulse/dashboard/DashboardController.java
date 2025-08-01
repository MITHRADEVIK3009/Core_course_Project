package com.example.airpulse.dashboard;

import com.example.airpulse.model.Alert;
import com.example.airpulse.model.SensorData;
import com.example.airpulse.repository.AlertRepository;
import com.example.airpulse.repository.SensorDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final SensorDataRepository sensorDataRepository;
    private final AlertRepository alertRepository;

    public DashboardController(SensorDataRepository sensorDataRepository, AlertRepository alertRepository) {
        this.sensorDataRepository = sensorDataRepository;
        this.alertRepository = alertRepository;
    }

    @GetMapping("/readings")
    public ResponseEntity<List<SensorData>> getLatestReadings() {
        // Retrieve the latest 100 readings ordered by timestamp descending
        PageRequest pageRequest = PageRequest.of(0, 100);
        List<SensorData> latestReadings = sensorDataRepository.findAllByOrderByTimestampDesc(pageRequest);
        return ResponseEntity.ok(latestReadings);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getLatestAlerts() {
        // Retrieve the latest 50 alerts ordered by timestamp descending
        PageRequest pageRequest = PageRequest.of(0, 50);
        List<Alert> latestAlerts = alertRepository.findAllByOrderByTimestampDesc(pageRequest);
        return ResponseEntity.ok(latestAlerts);
    }
} 