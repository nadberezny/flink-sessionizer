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
    private String campaign;
    private long timestamp;
    private long returnTimestamp;
    private int pageViewCount;
    private int durationMillis;
    private float total;
    private float shipping;
    private int weight;
    private String productId;
    private String productName;
    private float productPrice;
    private int productQuantity;
}
