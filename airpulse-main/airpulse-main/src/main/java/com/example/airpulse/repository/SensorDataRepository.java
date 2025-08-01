package com.example.airpulse.repository;

import com.example.airpulse.model.SensorData;
import com.example.airpulse.model.SensorType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, UUID> {
    List<SensorData> findAllByOrderByTimestampDesc(Pageable pageable);

    @Query("SELECT AVG(sd.value) FROM SensorData sd " +
           "JOIN Sensor s ON sd.sensorId = s.id " +
           "JOIN Location loc ON s.location.id = loc.id " +
           "JOIN Region r ON loc.region.id = r.id " +
           "WHERE sd.type = :sensorType AND r.name = :regionName AND sd.timestamp BETWEEN :startTime AND :endTime")
    Double findAverageValueBySensorTypeAndRegionInTimeRange(
            @Param("sensorType") SensorType sensorType,
            @Param("regionName") String regionName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT sd FROM SensorData sd " +
           "JOIN Sensor s ON sd.sensorId = s.id " +
           "JOIN Location loc ON s.location.id = loc.id " +
           "WHERE loc.region.id = :regionId ORDER BY sd.timestamp DESC LIMIT 1")
    Optional<SensorData> findTopBySensorLocationRegionIdOrderByTimestampDesc(@Param("regionId") UUID regionId);
} 