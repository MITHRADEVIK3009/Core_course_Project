package com.example.airpulse.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "sensor_id", nullable = false)
    private UUID sensorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType type;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String severity;

    @Column(name = "location_code", nullable = false)
    private String locationCode;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String region;
} 