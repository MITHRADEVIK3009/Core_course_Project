package com.example.airpulse.simulator;

import com.example.airpulse.dto.SensorReadingDto;
import com.example.airpulse.model.Location;
import com.example.airpulse.model.Region;
import com.example.airpulse.model.Sensor;
import com.example.airpulse.model.SensorType;
import com.example.airpulse.repository.SensorRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Profile("test") // Activate this simulator only for the 'test' profile
public class SensorSimulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorSimulator.class);
    private final SensorRepository sensorRepository;
    private final SensorProducer sensorProducer;
    private ScheduledExecutorService executorService;
    private final Random random = new Random();

    public SensorSimulator(SensorRepository sensorRepository, SensorProducer sensorProducer) {
        this.sensorRepository = sensorRepository;
        this.sensorProducer = sensorProducer;
    }

    @PostConstruct
    public void startSimulation() {
        List<Sensor> sensors = sensorRepository.findAll();
        if (sensors.isEmpty()) {
            LOGGER.warn("No sensors found in the database. Sensor simulation will not start.");
            return;
        }

        LOGGER.info("Found {} sensors. Starting simulation...", sensors.size());
        this.executorService = Executors.newScheduledThreadPool(Math.min(sensors.size(), 10)); // Thread pool size based on sensor count, max 10

        for (Sensor sensor : sensors) {
            // Schedule task with a slight initial random delay (0-4s) and then every 5 seconds
            executorService.scheduleAtFixedRate(() -> simulateAndSend(sensor), random.nextInt(5), 5, TimeUnit.SECONDS);
        }
        LOGGER.info("Sensor simulation scheduled for {} sensors.", sensors.size());
    }

    @Transactional
    private void simulateAndSend(Sensor sensor) {
        try {
            double value;
            String unit;

            switch (sensor.getType()) {
                case PM25:
                    value = random.nextDouble() * 250; // 0-250 µg/m³
                    unit = "µg/m³";
                    break;
                case PM10: // Assuming PM10 also uses µg/m³ and similar range for simulation
                    value = random.nextDouble() * 250; // 0-250 µg/m³
                    unit = "µg/m³";
                    break;
                case NO2:
                    value = random.nextDouble() * 200; // 0-200 ppb
                    unit = "ppb";
                    break;
                case CO:
                    value = random.nextDouble() * 50; // 0-50 ppm
                    unit = "ppm";
                    break;
                case TEMPERATURE:
                    value = -10 + (random.nextDouble() * 50); // -10 to 40 °C
                    unit = "°C";
                    break;
                case HUMIDITY:
                    value = 10 + (random.nextDouble() * 90); // 10-100 %
                    unit = "%";
                    break;
                case SO2:
                    value = random.nextDouble() * 100; // 0-100 ppb (example range)
                    unit = "ppb";
                    break;
                case O3:
                    value = random.nextDouble() * 150; // 0-150 ppb (example range)
                    unit = "ppb";
                    break;
                default:
                    LOGGER.warn("Unhandled sensor type: {}. Skipping simulation for this sensor.", sensor.getType());
                    return;
            }

            Location location = sensor.getLocation();
            Region region = location.getRegion();
            String cityName = region.getCity().getName();

            SensorReadingDto reading = SensorReadingDto.builder()
                    .sensorId(sensor.getId())
                    .timestamp(LocalDateTime.now())
                    .value(Math.round(value * 100.0) / 100.0) // Round to 2 decimal places
                    .unit(unit)
                    .type(sensor.getType())
                    .locationCode(location.getCodePrefix()) 
                    .city(cityName)
                    .region(region.getName())
                    .build();

            sensorProducer.sendSensorReading(reading);
        } catch (Exception e) {
            // Catching Exception to prevent one sensor's failure from stopping others
            LOGGER.error("Error during simulation for sensor ID {}: {}", sensor.getId(), e.getMessage(), e);
        }
    }

    @PreDestroy
    public void stopSimulation() {
        if (executorService != null && !executorService.isShutdown()) {
            LOGGER.info("Shutting down sensor simulation executor service...");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOGGER.error("Executor service did not terminate.");
                    }
                }
            } catch (InterruptedException ie) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            LOGGER.info("Sensor simulation stopped.");
        }
    }
} 