package com.example.airpulse.consumer;

import com.example.airpulse.consumer.model.AqiSeverity;
import com.example.airpulse.dto.AlertDto;
import com.example.airpulse.dto.SensorReadingDto;
import com.example.airpulse.model.Alert;
import com.example.airpulse.model.SensorData;
import com.example.airpulse.repository.AlertRepository;
import com.example.airpulse.repository.SensorDataRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SensorConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorConsumer.class);
    private static final String ALERTS_TOPIC_NAME = "alerts";
    private static final String SENSOR_DATA_DLQ_TOPIC_NAME = "sensor-data-dlq";

    private final AqiEvaluator aqiEvaluator;
    private final KafkaTemplate<String, AlertDto> alertKafkaTemplate;       // For sending actual alerts
    private final KafkaTemplate<String, SensorReadingDto> dlqKafkaTemplate; // For sending to DLQ
    private final SensorDataRepository sensorDataRepository;
    private final AlertRepository alertRepository;

    private final Counter messagesConsumedCounter;
    private final Counter alertsTriggeredCounter;
    private final Counter dlqReroutesCounter;

    public SensorConsumer(AqiEvaluator aqiEvaluator,
                          KafkaTemplate<String, AlertDto> alertKafkaTemplate,
                          KafkaTemplate<String, SensorReadingDto> dlqKafkaTemplate,
                          SensorDataRepository sensorDataRepository,
                          AlertRepository alertRepository,
                          MeterRegistry meterRegistry) {
        this.aqiEvaluator = aqiEvaluator;
        this.alertKafkaTemplate = alertKafkaTemplate;
        this.dlqKafkaTemplate = dlqKafkaTemplate;
        this.sensorDataRepository = sensorDataRepository;
        this.alertRepository = alertRepository;

        this.messagesConsumedCounter = Counter.builder("airpulse.consumer.messages.consumed")
                .description("Number of messages successfully consumed")
                .register(meterRegistry);
        this.alertsTriggeredCounter = Counter.builder("airpulse.consumer.alerts.triggered")
                .description("Number of alerts triggered by the consumer")
                .register(meterRegistry);
        this.dlqReroutesCounter = Counter.builder("airpulse.consumer.dlq.reroutes")
                .description("Number of messages rerouted to DLQ")
                .register(meterRegistry);
    }

    @KafkaListener(topics = "sensor-data", groupId = "airpulse-group")
    public void consumeSensorReading(@Payload SensorReadingDto reading, Acknowledgment acknowledgment) {
        try {
            LOGGER.info("Received sensor reading: ID={}, Type={}, Value={}, Location={}, City={}, Region={}",
                    reading.getSensorId(), reading.getType(), reading.getValue(),
                    reading.getLocationCode(), reading.getCity(), reading.getRegion());

            // Save the raw reading to the database
            saveSensorData(reading);

            AqiSeverity severity = aqiEvaluator.evaluateAqi(reading.getType(), reading.getValue());
            LOGGER.info("Evaluated AQI Severity for Sensor ID {}: {}", reading.getSensorId(), severity);

            if (aqiEvaluator.isAlertWorthy(severity)) {
                AlertDto alertDto = AlertDto.builder()
                        .sensorId(reading.getSensorId())
                        .value(reading.getValue())
                        .type(reading.getType())
                        .locationCode(reading.getLocationCode())
                        .severity(severity.name())
                        .timestamp(reading.getTimestamp())
                        .city(reading.getCity())
                        .region(reading.getRegion())
                        .build();
                sendAlert(alertDto);
                saveAlert(alertDto); // Save the alert to the database
                alertsTriggeredCounter.increment();
            }
            
            messagesConsumedCounter.increment(); // Increment after all processing steps (if successful till here)
            acknowledgment.acknowledge(); // Acknowledge after successful processing
            LOGGER.debug("Successfully processed and acknowledged sensor reading for ID: {}", reading.getSensorId());
        } catch (Exception e) {
            LOGGER.error("Processing failed for sensor reading ID {}. Sending to DLQ.", reading.getSensorId(), e);
            try {
                dlqKafkaTemplate.send(SENSOR_DATA_DLQ_TOPIC_NAME, reading.getSensorId().toString(), reading);
                dlqReroutesCounter.increment();
                LOGGER.info("Successfully sent message for sensor ID {} to DLQ topic: {}", reading.getSensorId(), SENSOR_DATA_DLQ_TOPIC_NAME);
            } catch (Exception dlqEx) {
                LOGGER.error("Failed to send message for sensor ID {} to DLQ topic {}: {}", reading.getSensorId(), SENSOR_DATA_DLQ_TOPIC_NAME, dlqEx.getMessage(), dlqEx);
            } finally {
                acknowledgment.acknowledge(); 
                LOGGER.warn("Acknowledged message for sensor ID {} after DLQ attempt.", reading.getSensorId());
            }
        }
    }

    @Transactional
    public void saveSensorData(SensorReadingDto reading) {
        try {
            SensorData data = SensorData.builder()
                    .sensorId(reading.getSensorId())
                    .timestamp(reading.getTimestamp())
                    .type(reading.getType())
                    .value(reading.getValue())
                    .build();
            sensorDataRepository.save(data);
            LOGGER.debug("Sensor reading saved to DB for sensor ID: {}", reading.getSensorId());
        } catch (Exception e) {
            LOGGER.error("Error saving sensor reading to DB for sensor ID {}: {}", reading.getSensorId(), e.getMessage(), e);
        }
    }

    @Transactional
    public void saveAlert(AlertDto alertDto) {
        try {
            Alert alert = Alert.builder()
                    .sensorId(alertDto.getSensorId())
                    .timestamp(alertDto.getTimestamp())
                    .type(alertDto.getType())
                    .value(alertDto.getValue())
                    .severity(alertDto.getSeverity())
                    .locationCode(alertDto.getLocationCode())
                    .city(alertDto.getCity())
                    .region(alertDto.getRegion())
                    .build();
            alertRepository.save(alert);
            LOGGER.debug("Alert saved to DB for sensor ID: {}", alertDto.getSensorId());
        } catch (Exception e) {
            LOGGER.error("Error saving alert to DB for sensor ID {}: {}", alertDto.getSensorId(), e.getMessage(), e);
        }
    }

    private void sendAlert(AlertDto alert) {
        try {
            alertKafkaTemplate.send(ALERTS_TOPIC_NAME, alert.getSensorId().toString(), alert);
            LOGGER.warn("ALERT SENT to Kafka topic '{}': Sensor ID {}, Severity {}, Location {}",
                    ALERTS_TOPIC_NAME, alert.getSensorId(), alert.getSeverity(), alert.getLocationCode());
        } catch (Exception e) {
            LOGGER.error("Error sending alert to Kafka for sensor ID {}: {}", alert.getSensorId(), e.getMessage(), e);
            throw e; // Re-throw to be caught by the main DLQ handler if this sending is critical
        }
    }
} 