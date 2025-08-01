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
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "sensor_id", nullable = false)
    private UUID sensorId; // Maps to existing sensor_id column in the database

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType type;

    @Column(nullable = false)
    private Double value;
    
    // Not linking directly to Location entity to keep this table focused on raw data from Kafka message
    // and avoid complex joins if this table grows very large. Location info is in SensorReadingDto if needed for denormalization.
    // If direct relation to Location is desired, a @ManyToOne could be added:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "location_id")
    // private Location location;
} 