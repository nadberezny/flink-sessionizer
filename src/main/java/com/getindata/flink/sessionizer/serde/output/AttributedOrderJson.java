package com.getindata.flink.sessionizer.serde.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributedOrderJson {
    private String orderId;
    private String sessionId;
    private String userId;
    private String marketingChannel;
    private long timestamp;
    private int pageViewCount;
    private int durationMillis;
    private float total;
    private float shipping;
    private int weight;
}
