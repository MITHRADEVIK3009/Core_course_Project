package com.example.airpulse.controller;

import com.example.airpulse.model.City;
import com.example.airpulse.model.Location;
import com.example.airpulse.model.Region;
import com.example.airpulse.model.Sensor;
import com.example.airpulse.repository.CityRepository;
import com.example.airpulse.repository.LocationRepository;
import com.example.airpulse.repository.RegionRepository;
import com.example.airpulse.repository.SensorRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/view") // Added a base path for clarity
@Profile("test") // This controller will only be active during tests or if 'test' profile is active
public class DataViewController {

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final LocationRepository locationRepository;
    private final SensorRepository sensorRepository;

    public DataViewController(CityRepository cityRepository,
                              RegionRepository regionRepository,
                              LocationRepository locationRepository,
                              SensorRepository sensorRepository) {
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.locationRepository = locationRepository;
        this.sensorRepository = sensorRepository;
    }

    @GetMapping("/cities")
    public ResponseEntity<List<City>> getCities() {
        return ResponseEntity.ok(cityRepository.findAll());
    }

    @GetMapping("/regions")
    public ResponseEntity<List<Region>> getRegionsByCity(@RequestParam UUID cityId) {
        return ResponseEntity.ok(regionRepository.findByCityId(cityId));
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getLocationsByRegion(@RequestParam UUID regionId) {
        return ResponseEntity.ok(locationRepository.findByRegionId(regionId));
    }

    @GetMapping("/sensors")
    public ResponseEntity<List<Sensor>> getSensorsByLocation(@RequestParam UUID locationId) {
        return ResponseEntity.ok(sensorRepository.findByLocationId(locationId));
    }
} 