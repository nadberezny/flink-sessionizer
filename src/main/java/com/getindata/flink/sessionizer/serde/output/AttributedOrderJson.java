package com.getindata.flink.sessionizer.serde.output;

public record AttributedOrderJson(String orderId,
                                  String sessionId,
                                  String userId,
                                  String marketingChannel,
                                  long timestamp,
                                  int pageViewCount,
                                  int durationMillis,
                                  float total,
                                  float shipping,
                                  int weight) {
}
