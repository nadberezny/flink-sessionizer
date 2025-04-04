package com.getindata.flink.sessionizer.serde.output;

public record OrderWithAttributedSessions(String orderId,
                                          String sessionId,
                                          String userId,
                                          long timestamp,
                                          int pageViewCount,
                                          int durationMillis) {
}
