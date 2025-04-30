package com.getindata.sessionizer.datagen.serde.kafka;

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
