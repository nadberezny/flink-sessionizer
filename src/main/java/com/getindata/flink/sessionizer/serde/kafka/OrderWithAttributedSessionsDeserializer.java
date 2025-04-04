package com.getindata.flink.sessionizer.serde.kafka;

import com.getindata.flink.sessionizer.serde.output.OrderWithAttributedSessions;

public class OrderWithAttributedSessionsDeserializer extends JsonDeserializer<OrderWithAttributedSessions> {

    public OrderWithAttributedSessionsDeserializer() {
        super(OrderWithAttributedSessions.class);
    }
}
