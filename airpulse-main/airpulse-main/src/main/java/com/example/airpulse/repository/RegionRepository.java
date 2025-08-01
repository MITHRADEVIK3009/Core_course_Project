package com.example.airpulse.repository;

import com.example.airpulse.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegionRepository extends JpaRepository<Region, UUID> {
    List<Region> findByCityId(UUID cityId);
} 