package com.example.airpulse.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("kafka") // Naming the health indicator
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;
    private static final int TIMEOUT_MS = 5000; // 5 seconds timeout for Kafka check

    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeClusterOptions options = new DescribeClusterOptions().timeoutMs(TIMEOUT_MS);
            DescribeClusterResult cluster = adminClient.describeCluster(options);
            String clusterId = cluster.clusterId().get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            int nodeCount = cluster.nodes().get(TIMEOUT_MS, TimeUnit.MILLISECONDS).size();
            if (nodeCount > 0) {
                return Health.up()
                        .withDetail("clusterId", clusterId)
                        .withDetail("nodes", nodeCount)
                        .withDetail("message", "Kafka connection is OK.")
                        .build();
            } else {
                return Health.down()
                        .withDetail("message", "Kafka cluster has no active nodes.")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("message", "Kafka connection failed.")
                    .withException(e)
                    .build();
        }
    }
} 