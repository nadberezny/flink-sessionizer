package com.getindata.flink.sessionizer.serde.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getindata.flink.sessionizer.model.OrderWithSessions;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class OrderWithSessionsSerializationSchema implements SerializationSchema<OrderWithSessions> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(OrderWithSessions orderWithSessions) {
        try {
            return objectMapper.writeValueAsBytes(orderWithSessions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize OrderWithSessions", e);
        }
    }
}
