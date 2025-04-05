package com.getindata.flink.sessionizer.model;

import com.getindata.flink.sessionizer.model.event.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public final class Session {

    private String id;
    private long timestamp;
    private Key userId;
    private long windowFrom;
    private long windowTo;
    private int pageViewCount;
    private long durationMillis;
    private MarketingChannel marketingChannel;
    private String landingPage;
    private ClickStreamEvent lastEvent;

    public boolean isOrder() {
        return lastEvent.getOrder() != null;
    }

    public Order getOrder() {
        return isOrder() ? lastEvent.getOrder() : null;
    }

    public Session merge(Session other) {
        this.durationMillis = (this.durationMillis + other.durationMillis);
        this.pageViewCount = (this.pageViewCount + other.pageViewCount);
        return this;
    }
}
