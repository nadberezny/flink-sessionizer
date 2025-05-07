package com.getindata.sessionizer.datagen;

import com.getindata.sessionizer.datagen.conf.ProducerType;
import com.getindata.sessionizer.datagen.generators.OrderGenerator;
import com.getindata.sessionizer.datagen.producer.KafkaProducer;
import com.getindata.sessionizer.datagen.producer.Producer;
import com.getindata.sessionizer.datagen.producer.TrackeriTwoProducer;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    private final static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public static AppConfig appConfig;

    public static Producer producer;


    public static void main(String[] args) {
        setup();
        scheduleUsers(new OrderGenerator());
    }

    private static void setup() {
        appConfig = new AppConfig(ConfigFactory.load());
        if (appConfig.isDryRun) {
            log.info("Dry Run");
        } else if (appConfig.producerType == ProducerType.KAFKA) {
            producer = new KafkaProducer(appConfig.kafkaConfig);
        } else if (appConfig.producerType == ProducerType.T2) {
            producer = new TrackeriTwoProducer(appConfig.t2Config.getBaseUrl());
        } else {
            throw new IllegalArgumentException("Unknown producer type: " + appConfig.producerType);
        }
    }

    private static void scheduleUsers(OrderGenerator orderGenerator) {
        long scheduleUsersIntervalMs = appConfig.scheduleUsersInterval.toMillis();
        new UserBuckets(appConfig.maxActiveUsers)
                .toList()
                .parallelStream()
                .forEach(userBucket ->
                        scheduledExecutor.scheduleWithFixedDelay(
                                new UserScheduler(userBucket, appConfig.isDryRun, orderGenerator), scheduleUsersIntervalMs, scheduleUsersIntervalMs, TimeUnit.MILLISECONDS
                        )
                );
    }
}
