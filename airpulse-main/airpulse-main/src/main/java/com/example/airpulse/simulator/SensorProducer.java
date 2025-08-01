package com.example.airpulse.simulator;

import com.example.airpulse.dto.SensorReadingDto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SensorProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorProducer.class);
    private static final String TOPIC_NAME = "sensor-data";

    private final KafkaTemplate<String, SensorReadingDto> kafkaTemplate;
    private final Counter messagesSentCounter;

    public SensorProducer(KafkaTemplate<String, SensorReadingDto> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.messagesSentCounter = Counter.builder("airpulse.producer.messages.sent")
                .description("Number of messages successfully sent by the producer")
                .register(meterRegistry);
    }

    public void sendSensorReading(SensorReadingDto reading) {
        try {
            // Using sensorId as key for partitioning, ensuring readings from the same sensor go to the same partition
            kafkaTemplate.send(TOPIC_NAME, reading.getSensorId().toString(), reading);
            messagesSentCounter.increment(); // Increment counter on successful send
            LOGGER.info("Sent sensor reading to Kafka: Sensor ID {}, Type {}, Value {}, Location {}",
                    reading.getSensorId(), reading.getType(), reading.getValue(), reading.getLocationCode());
        } catch (Exception e) {
            LOGGER.error("Error sending sensor reading to Kafka for sensor ID {}: {}", reading.getSensorId(), e.getMessage());
            // Optionally, could add a counter for send failures here
        }
    }
} 