package com.getindata.flink.sessionizer.config;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public class JobConfig {

    private final String bootStrapServers;
    private final String clickStreamTopic;
    private final String sessionsTopic;
    private final String attributedOrdersTopic;
    private final Duration sessionInactivityGap;

    public JobConfig(Config config) {
        var jobConfig = config.getConfig("job");
        bootStrapServers = jobConfig.getString("bootStrapServers");
        clickStreamTopic = jobConfig.getString("clickStreamTopic");
        sessionsTopic = jobConfig.getString("sessionsTopic");
        attributedOrdersTopic = jobConfig.getString("attributedOrdersTopic");
        sessionInactivityGap = Duration.parse(jobConfig.getString("sessionInactivityGap"));
    }
}
