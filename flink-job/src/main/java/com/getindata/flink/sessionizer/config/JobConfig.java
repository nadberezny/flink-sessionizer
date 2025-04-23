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

    private final String attributionServiceUrl;

    private final KafkaConfig kafkaConfig;

    private final CDCConfig cdcConfig;

    public JobConfig(Config config) {
        var jobConfig = config.getConfig("job");
        sessionInactivityGap = Duration.parse(jobConfig.getString("sessionInactivityGap"));
        attributionServiceUrl = jobConfig.getString("attribution.serviceUrl");
        kafkaConfig = new KafkaConfig(jobConfig);
        cdcConfig = new CDCConfig(jobConfig);
    }
}
