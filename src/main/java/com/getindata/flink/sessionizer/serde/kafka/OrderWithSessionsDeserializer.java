package com.getindata.flink.sessionizer.serde.kafka;

import com.getindata.flink.sessionizer.model.OrderWithSessions;

public class OrderWithSessionsDeserializer extends JsonDeserializer<OrderWithSessions> {

    public OrderWithSessionsDeserializer() {
        super(OrderWithSessions.class);
    }
}
