package com.getindata.flink.sessionizer.serde.input;

public record PageView(
    String uid,
    String productId,
    String url,
    String channel,
    String channelGroup,
    String campaign,
    String referrer,
    String country,
    String userAgent,
    String ip,
    String impressionId
) {

}
