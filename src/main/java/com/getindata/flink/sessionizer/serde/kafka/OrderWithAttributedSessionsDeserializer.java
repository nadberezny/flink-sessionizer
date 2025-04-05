package com.getindata.flink.sessionizer.serde.kafka;

import com.getindata.flink.sessionizer.serde.output.AttributedOrderJson;

public class OrderWithAttributedSessionsDeserializer extends JsonDeserializer<AttributedOrderJson> {

    public OrderWithAttributedSessionsDeserializer() {
        super(AttributedOrderJson.class);
    }
}
