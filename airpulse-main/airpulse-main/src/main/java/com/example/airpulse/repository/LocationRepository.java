package com.example.airpulse.repository;

import com.example.airpulse.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByRegionId(UUID regionId);
} 