package com.getindata.flink.sessionizer.config;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.Duration;

@Getter
@RequiredArgsConstructor
public class JobConfig implements Serializable {

    private final Duration sessionInactivityGap;
    private final KafkaConfig kafkaConfig;

    public JobConfig(Config config) {
        var jobConfig = config.getConfig("job");
        sessionInactivityGap = Duration.parse(jobConfig.getString("sessionInactivityGap"));
        kafkaConfig = new KafkaConfig(config);
    }
}
