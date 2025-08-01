package com.example.airpulse.repository;

import com.example.airpulse.model.Alert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findAllByOrderByTimestampDesc(Pageable pageable);

    Long countBySeverityAndTimestampBetween(String severity, LocalDateTime startTime, LocalDateTime endTime);
} 