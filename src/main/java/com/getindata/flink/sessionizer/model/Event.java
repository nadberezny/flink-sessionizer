package com.getindata.flink.sessionizer.model;

import com.getindata.flink.sessionizer.model.event.Order;
import com.getindata.flink.sessionizer.model.event.PageView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Event {

    private long timestamp;

    private String timeZone;

    private Key key;

    private PageView pageView;

    private Order order;

    @JsonIgnore
    @JsonProperty("type")
    public String getType() {
        if (pageView != null) {
            return "pageView";
        } else if (order != null) {
            return "order";
        } else {
            return "unknown";
        }
    }
}
