package com.example.airpulse.service;

import com.example.airpulse.model.*;
import com.example.airpulse.repository.CityRepository;
import com.example.airpulse.repository.LocationRepository;
import com.example.airpulse.repository.RegionRepository;
import com.example.airpulse.repository.SensorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final LocationRepository locationRepository;
    private final SensorRepository sensorRepository;

    public DataSeeder(CityRepository cityRepository,
                      RegionRepository regionRepository,
                      LocationRepository locationRepository,
                      SensorRepository sensorRepository) {
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.locationRepository = locationRepository;
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (cityRepository.count() == 0) { // Seed only if DB is empty
            seedData();
        }
    }

    private void seedData() {
        // City 1: Toronto
        City toronto = City.builder().name("Toronto").build();
        cityRepository.save(toronto);

        Region downtownToronto = Region.builder().name("Downtown Toronto").city(toronto).build();
        Region northYork = Region.builder().name("North York").city(toronto).build();
        Region scarborough = Region.builder().name("Scarborough").city(toronto).build();
        regionRepository.saveAll(Arrays.asList(downtownToronto, northYork, scarborough));

        // Locations for Downtown Toronto
        createLocationsForRegion(downtownToronto, "Financial District", "Entertainment District", "Kensington Market", "Yorkville", "Discovery District");

        // Locations for North York
        createLocationsForRegion(northYork, "Willowdale", "Bayview Village", "Don Mills", "York University Heights", "Newtonbrook");

        // Locations for Scarborough
        createLocationsForRegion(scarborough, "Agincourt", "Malvern", "Guildwood", "West Hill", "Highland Creek");

        // City 2: Montreal
        City montreal = City.builder().name("Montreal").build();
        cityRepository.save(montreal);

        Region downtownMontreal = Region.builder().name("Downtown Montreal").city(montreal).build();
        Region plateauMontRoyal = Region.builder().name("Plateau Mont-Royal").city(montreal).build();
        Region oldMontreal = Region.builder().name("Old Montreal").city(montreal).build();
        regionRepository.saveAll(Arrays.asList(downtownMontreal, plateauMontRoyal, oldMontreal));

        // Locations for Downtown Montreal
        createLocationsForRegion(downtownMontreal, "Golden Square Mile", "Quartier des Spectacles", "Chinatown", "International District", "Shaughnessy Village");

        // Locations for Plateau Mont-Royal
        createLocationsForRegion(plateauMontRoyal, "Mile End", "Jeanne-Mance", "De Lorimier", "Little Portugal", "Outremont North");

        // Locations for Old Montreal
        createLocationsForRegion(oldMontreal, "Old Port", "Saint-Paul Street", "Notre-Dame Basilica Area", "Bonsecours Market", "Pointe-à-Callière");

        System.out.println("Database seeded successfully with " + locationRepository.count() + " locations and " + sensorRepository.count() + " sensors.");
    }

    private void createLocationsForRegion(Region region, String loc1Name, String loc2Name, String loc3Name, String loc4Name, String loc5Name) {
        List<LocationType> types = Arrays.asList(LocationType.APARTMENT, LocationType.HOUSE, LocationType.HOSPITAL);
        List<String> names = Arrays.asList(loc1Name, loc2Name, loc3Name, loc4Name, loc5Name);

        for (int i = 0; i < names.size(); i++) {
            LocationType type = types.get(i % types.size()); // Cycle through types
            Location location = Location.builder()
                    .name(names.get(i) + " " + type.name().toLowerCase().replace("_", " "))
                    .type(type)
                    .region(region)
                    .build(); // codePrefix is set via @PrePersist
            locationRepository.save(location);

            // Add 2 sensors per location
            Sensor sensor1 = Sensor.builder().type(SensorType.PM25).location(location).build();
            Sensor sensor2 = Sensor.builder().type(SensorType.NO2).location(location).build();
            sensorRepository.saveAll(Arrays.asList(sensor1, sensor2));
        }
    }
} 