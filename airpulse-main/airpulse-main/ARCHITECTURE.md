# System Architecture

The architecture diagram below illustrates the overall design and components of the **AirPulse: Air-Raitm Air & Health Monitoring** system.

![architecture DIagram](./image1)

## Components Overview

- **Wearable Edge Device (Arduino/ESP32):** Collects environmental and health data from onboard sensors.
- **EdgeFrame Threshold Engine:** Processes sensor data and triggers events when thresholds are crossed.
- **Sensors:** Includes PM 2.5, VOC, CO₂, Temperature, and Heart Rate sensors.
- **Forgeframe Forgetful Forstos:** Manages data flow between edge and cloud.
- **Consumer & Alert Services:** Consumes data and triggers alerts.
- **Database (PostgreSQL):** Stores sensor and system data.
- **Kafka Broker (Redpanda):** Message broker for data streaming.
- **API Gateway:** Manages API requests.
- **Redis:** Caching and fast data access.
- **Prometheus + Grafana:** Monitoring and visualization.

> For more details, refer to each module’s documentation.