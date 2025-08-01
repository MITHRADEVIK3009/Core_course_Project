# AirPulse: Real-time Air Quality Monitoring System


AirPulse is a comprehensive real-time air quality monitoring system that uses a modern tech stack to collect, process, store, analyze, and visualize air quality data from multiple sensors.

## Table of Contents

- [Overview](#overview)
- [Project Architecture](#project-architecture)
- [Tech Stack](#tech-stack)
- [Key Features](#key-features)
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Database Setup](#database-setup)
  - [Kafka Setup](#kafka-setup)
  - [Application Setup](#application-setup)
  - [Redis Cache Setup](#redis-cache-setup)
  - [Monitoring Setup](#monitoring-setup)
- [Running the Application](#running-the-application)
- [Development Phases](#development-phases)
- [API Documentation](#api-documentation)
- [Monitoring and Observability](#monitoring-and-observability)
- [Contributing](#contributing)
- [License](#license)

## Overview

AirPulse is a project designed to simulate, process, and analyze air quality data in near real-time. The system collects data from multiple virtual sensors, processes the readings, triggers alerts based on configurable thresholds, and provides analytical insights through RESTful APIs and visualizations.

## Project Architecture

AirPulse follows a microservices-inspired architecture with the following components:

1. **Sensor Simulator**: Generates synthetic sensor readings
2. **Kafka Message Broker**: Handles message queuing with topics for readings and alerts
3. **Consumer Service**: Processes readings, evaluates AQI, triggers alerts
4. **Database Layer**: Stores readings, alerts, and metadata
5. **Redis Cache**: Caches expensive analytics queries
6. **Analytics API**: Provides statistical insights and historical data
7. **Dashboard API**: Serves data to visualization tools
8. **Monitoring Stack**: Prometheus and Grafana for monitoring

## Tech Stack

- **Java 17**: Core programming language
- **Spring Boot 3.2.5**: Application framework
- **Spring Data JPA**: Data access layer
- **Spring Kafka**: Message processing
- **PostgreSQL**: Main database
- **Redis**: Caching layer
- **Lombok**: Boilerplate code reduction
- **Kafka (Redpanda)**: Message broker
- **Docker**: Containerization
- **Prometheus**: Metrics collection
- **Grafana**: Visualization and alerting
- **Maven**: Dependency management and build tool

## Key Features

- Real-time sensor data simulation and processing
- Kafka-based messaging with Dead Letter Queue (DLQ) for error handling
- Air Quality Index (AQI) evaluation based on EPA standards
- Alert generation for unhealthy air quality conditions
- RESTful APIs for analytics and dashboard data
- Redis caching for improved query performance
- Comprehensive monitoring and observability with Prometheus and Grafana
- Fault tolerance with error handling and message redelivery
- Persistent storage of readings and alerts

## Installation

### Prerequisites

- Java 17 or later
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL 13+
- Redis 6+
- Prometheus
- Grafana

### Database Setup

1. **Start PostgreSQL**:

```bash
# Using Docker
docker run --name postgres-airpulse -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=airpulse_db -p 5432:5432 -d postgres:latest

# Alternatively, if you have PostgreSQL installed locally:
createdb airpulse_db
```

2. **Verify Connection**:

```bash
psql -U postgres -h localhost -p 5432 -d airpulse_db -c "SELECT 1"
```

### Kafka Setup

We're using Redpanda, a Kafka-compatible message broker with improved performance:

```bash
# Start Redpanda using Docker Compose
docker-compose up -d
```

Create the required topics:

```bash
# Create sensor-data topic
docker exec -it redpanda rpk topic create sensor-data

# Create alerts topic
docker exec -it redpanda rpk topic create alerts

# Create DLQ topic for error handling
docker exec -it redpanda rpk topic create sensor-data-dlq
```

Verify topics were created:

```bash
docker exec -it redpanda rpk topic list
```

### Application Setup

1. **Clone the repository**:

```bash
git clone https://github.com/djlord-it/airpulse.git
cd airpulse
```

2. **Build the application**:

```bash
mvn clean package
```

### Redis Cache Setup

Start Redis:

```bash
# Using Docker
docker run --name redis-airpulse -p 6379:6379 -d redis:latest

# Alternatively, if Redis is installed locally:
redis-server
```

Verify Redis connection:

```bash
redis-cli ping
```

### Monitoring Setup

1. **Start Prometheus**:

Create a `prometheus.yml` configuration file:

```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'airpulse'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8081']
```

Start Prometheus:

```bash
prometheus --config.file=/path/to/prometheus.yml
```

2. **Start Grafana**:

```bash
# On macOS with Homebrew
brew services start grafana

# On Linux
systemctl start grafana-server
```

Configure Grafana data source:
- Access Grafana UI at http://localhost:3000 (default credentials: admin/admin)
- Add Prometheus data source (http://localhost:9090)

## Running the Application

1. **Start the application**:

```bash
# Using the test profile for Redis caching
SPRING_PROFILES_ACTIVE=test mvn spring-boot:run
```

2. **Access the application**:
   - Main API: http://localhost:8080
   - Actuator endpoints: http://localhost:8081/actuator
   - Dashboard: http://localhost:8080/

3. **Monitor the application**:
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000

## Development Phases

The project was developed in multiple phases:

### Phase 1: Database Setup
- Entity modeling (City, Region, Location, Sensor)
- Repository layer implementation
- Data seeder for test data

### Phase 2: Kafka Producer
- SensorSimulator to generate synthetic readings
- SensorProducer to send data to Kafka

### Phase 3: Kafka Consumer
- SensorConsumer to process readings
- AQI evaluation logic
- Alert generation

### Phase 4: Dashboard API
- REST endpoints for sensor data
- Frontend HTML/JS dashboard

### Phase 5: Analytics API
- Statistical endpoints for air quality data
- Aggregation and summary functionality

### Phase 6: Redis Caching
- Implementation of caching for expensive queries
- TTL configuration

### Phase 8: Dead Letter Queue (DLQ)
- Error handling and failed message routing
- Manual acknowledgment of Kafka messages

### Phase 9: Monitoring and Observability
- Spring Boot Actuator integration
- Custom health indicators
- Micrometer metrics
- Prometheus and Grafana integration

## API Documentation

### Core Endpoints

- `GET /api/readings`: Latest 100 sensor readings
- `GET /api/alerts`: Latest 50 alerts

### Analytics Endpoints

- `GET /api/average?type=PM25&region=Downtown`: Average value of sensor type in region
- `GET /api/alerts/count?severity=VERY_UNHEALTHY`: Count of alerts by severity
- `GET /api/summary?city=Montreal`: City summary with AQI levels by region

### Actuator Endpoints

- `GET /actuator/health`: Application health status
- `GET /actuator/info`: Application information
- `GET /actuator/metrics`: Available metrics
- `GET /actuator/prometheus`: Prometheus-formatted metrics

## Monitoring and Observability

### Custom Metrics

- `airpulse.producer.messages.sent`: Count of messages sent by producer
- `airpulse.consumer.messages.consumed`: Count of messages consumed
- `airpulse.consumer.alerts.triggered`: Count of alerts triggered
- `airpulse.consumer.dlq.reroutes`: Count of messages sent to DLQ

### Health Indicators

- `database`: PostgreSQL connectivity check
- `kafka`: Kafka cluster connectivity check

### Dashboard Setup

1. **Create Grafana Dashboard**:
   - Time series panel for message throughput
   - Counter panels for alerts and DLQ messages
   - Gauge for system resource utilization
   - Heatmap for regional air quality

2. **Example PromQL Queries**:
   - Message Rate: `rate(airpulse_producer_messages_sent_total[1m])`
   - Alert Rate: `rate(airpulse_consumer_alerts_triggered_total[1m])`
   - JVM Memory: `jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100`

## Terminal Commands Reference

### Docker/Container Management

```bash
# View running containers
docker ps

# Container logs
docker logs redpanda

# Stop containers
docker stop postgres-airpulse redis-airpulse redpanda
```

### Kafka/Redpanda Commands

```bash
# List topics
docker exec -it redpanda rpk topic list

# Describe topic
docker exec -it redpanda rpk topic describe sensor-data

# Console consumer (for debugging)
docker exec -it redpanda rpk topic consume sensor-data -f '%k: %v\n'

# Console producer (for testing)
docker exec -it redpanda rpk topic produce sensor-data
```

### Database Commands

```bash
# Connect to PostgreSQL
psql -U postgres -h localhost -d airpulse_db

# List tables
psql -U postgres -h localhost -d airpulse_db -c "\dt"

# Query data
psql -U postgres -h localhost -d airpulse_db -c "SELECT * FROM sensor_data LIMIT 10"
```

### Application Commands

```bash
# Start application (development)
SPRING_PROFILES_ACTIVE=test mvn spring-boot:run

# Start application (production)
java -jar -Dspring.profiles.active=prod target/airpulse-0.0.1-SNAPSHOT.jar

# Run tests
mvn test
```

### Monitoring Commands

```bash
# Check Prometheus targets
curl http://localhost:9090/api/v1/targets

# Query metrics
curl http://localhost:9090/api/v1/query?query=up

# Check Spring Boot Actuator health
curl http://localhost:8081/actuator/health
```


## License

MIT.