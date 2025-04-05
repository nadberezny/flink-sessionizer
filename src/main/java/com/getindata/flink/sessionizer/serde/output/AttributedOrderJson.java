package com.getindata.flink.sessionizer.serde.output;

public record AttributedOrderJson(String orderId,
                                  String sessionId,
                                  String userId,
                                  long timestamp,
                                  int pageViewCount,
                                  int durationMillis) {
}
