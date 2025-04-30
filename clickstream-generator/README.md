# Clickstream Generator

A Java application that simulates user sessions and generates events (PageViews and Orders) that are sent to Kafka or other configured destinations.

## Overview

This data generator is designed to simulate user behavior by creating virtual users that browse pages and occasionally place orders. It's useful for testing and benchmarking systems that process user event data.

Key features:
- Configurable number of concurrent users
- Realistic session durations based on configurable buckets
- Generation of PageView and Order events
- Support for different output destinations (Kafka, T2)
- Configurable event generation rates
- Dry run mode for testing without sending events

## Prerequisites

- Java 21 or higher
- Gradle (for building)
- Kafka (if using Kafka producer)

## Building

Build the application using Gradle:

```bash
./gradlew build
```

Create a distribution:

```bash
./gradlew installDist
```

## Configuration

The application is configured using the `application.conf` file and environment variables. Here are the main configuration options:

### Application Configuration

| Parameter | Description | Default | Environment Variable |
|-----------|-------------|---------|---------------------|
| frontendId | Identifier for the frontend | "load_test_v1" | FRONTEND_ID |
| isDryRun | If true, events are not sent to the destination | false | IS_DRY_RUN |
| scheduleUsersInterval | Interval for scheduling user sessions | "PT0.5S" | SCHEDULE_USERS_INTERVAL |
| maxActiveUsers | Maximum number of concurrent users | 100 | MAX_ACTIVE_USERS |
| producerType | Type of producer (KAFKA or T2) | "KAFKA" | PRODUCER_TYPE |

### Kafka Configuration

| Parameter | Description | Default | Environment Variable |
|-----------|-------------|---------|---------------------|
| bootstrapServers | Kafka bootstrap servers | "localhost:63795" | KAFKA_BOOTSTRAP_SERVERS |
| outputTopic | Kafka topic to send events to | "input" | KAFKA_OUTPUT_TOPIC |
| securityProtocol | Kafka security protocol | "PLAINTEXT" | KAFKA_SECURITY_PROTOCOL |
| sasl.jaas.config | SASL JAAS configuration | null | KAFKA_SASL_JAAS_CONFIG |
| sasl.mechanism | SASL mechanism | "PLAIN" | KAFKA_SASL_MECHANISM |

## Running

### Local Execution

Run the application using the Gradle distribution:

```bash
./build/install/datagen/bin/datagen
```

### Docker

Build the Docker image:

```bash
./gradlew installDist
docker build -t data-generator .
```

Run the Docker container:

```bash
docker run -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 -e KAFKA_OUTPUT_TOPIC=events data-generator
```

### Local Development Environment

A Docker Compose file is provided for setting up a local development environment with Kafka, Zookeeper, and AKHQ (Kafka UI):

```bash
docker-compose up -d
```

This will start:
- Zookeeper on the default port (2181)
- Kafka on port 49816
- AKHQ (Kafka UI) on port 8080

### Kubernetes

The application can be deployed to Kubernetes using the provided Helm chart:

```bash
helm upgrade --install data-gen ./k8s-helm --namespace=your-namespace
```

Configure the deployment by modifying the values in `k8s-helm/values.yaml` or by providing custom values:

```bash
helm upgrade --install data-gen ./k8s-helm --namespace=your-namespace --set maxActiveUsers=50
```

## Event Types

The application generates two types of events:

### PageView

Represents a user viewing a page. Includes information such as:
- Visitor ID
- URL
- Channel (e.g., Facebook, Google)
- Country
- User agent
- IP address

### Order

Represents a user placing an order. Includes information such as:
- Visitor ID
- Order ID
- Products
- Vouchers
- Payment method
- Currency
- Country
- User agent
- IP address

## User Simulation

Users are simulated with the following characteristics:
- Session duration based on configurable buckets
- Page view intervals based on the bucket
- Chance to place an order at the end of the session based on the bucket
