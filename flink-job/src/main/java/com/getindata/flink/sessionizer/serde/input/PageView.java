package com.getindata.flink.sessionizer.serde.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageView {
    private String uid;
    private String productId;
    private String url;
    private String channel;
    private String channelGroup;
    private String campaign;
    private String referrer;
    private String country;
    private String userAgent;
    private String ip;
    private String impressionId;
}
