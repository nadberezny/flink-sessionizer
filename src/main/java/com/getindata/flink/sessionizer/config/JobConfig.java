package com.getindata.flink.sessionizer.config;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public class JobConfig {

    private final String bootStrapServers;
    private final String inputTopic;
    private final String outputTopic;
    private final Duration sessionInactivityGap;

    public JobConfig(Config config) {
        var jobConfig = config.getConfig("job");
        bootStrapServers = jobConfig.getString("bootStrapServers");
        inputTopic = jobConfig.getString("inputTopic");
        outputTopic = jobConfig.getString("outputTopic");
        sessionInactivityGap = Duration.parse(jobConfig.getString("sessionInactivityGap"));
    }
}
