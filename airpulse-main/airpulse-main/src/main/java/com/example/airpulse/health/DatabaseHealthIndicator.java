package com.example.airpulse.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("database") // Naming the health indicator
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // A simple query to check if the database is reachable and responsive
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return Health.up().withDetail("message", "Database connection is OK.").build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("message", "Database connection failed.")
                    .withException(e)
                    .build();
        }
    }
} 