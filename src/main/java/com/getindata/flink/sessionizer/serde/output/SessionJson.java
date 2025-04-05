package com.getindata.flink.sessionizer.serde.output;

public record SessionJson(String sessionId,
                          String userId,
                          long timestamp,
                          int pageViewCount,
                          int durationMillis) {
}
