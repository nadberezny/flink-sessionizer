package com.getindata.sessionizer.datagen.serde.t2;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder(setterPrefix = "with", toBuilder = true)
public class TrackingRequest {
    private String frontendId;
    private String url;
    private MetaInfo metaInfo;
    private String referrer;
    private String uid;
    private String ip;
    private String userAgent;
    private String impressionId;
    private String eventType;
    private Map<String, String> source;
    private String version;
    private Instant createdAt;
}
