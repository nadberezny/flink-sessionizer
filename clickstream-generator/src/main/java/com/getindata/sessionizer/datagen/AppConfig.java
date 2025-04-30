package com.getindata.sessionizer.datagen;

import com.getindata.sessionizer.datagen.conf.KafkaConfig;
import com.getindata.sessionizer.datagen.conf.ProducerType;
import com.getindata.sessionizer.datagen.conf.T2Config;
import com.typesafe.config.Config;

import java.time.Duration;

public class AppConfig {

    String frontendId;

    Duration scheduleUsersInterval;

    long maxActiveUsers;

    boolean isDryRun;

    ProducerType producerType;

    KafkaConfig kafkaConfig;

    T2Config t2Config;

    public AppConfig(Config conf) {
        var appConfig = conf.getConfig("app");
        scheduleUsersInterval = Duration.parse(appConfig.getString("scheduleUsersInterval"));
        maxActiveUsers = appConfig.getLong("maxActiveUsers");
        frontendId = appConfig.getString("frontendId");
        isDryRun = appConfig.getBoolean("isDryRun");
        producerType = ProducerType.valueOf(appConfig.getString("producerType"));
        kafkaConfig = new KafkaConfig(conf);
        t2Config = new T2Config(conf);
    }
}
