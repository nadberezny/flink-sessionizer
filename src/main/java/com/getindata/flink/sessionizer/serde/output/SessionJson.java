package com.getindata.flink.sessionizer.serde.output;

public record SessionJson(String sessionId,
                          String userId,
                          String marketingChannel,
                          long timestamp,
                          int pageViewCount,
                          int durationMillis) {
}
