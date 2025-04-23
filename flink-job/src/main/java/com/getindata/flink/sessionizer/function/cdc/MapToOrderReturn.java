package com.getindata.flink.sessionizer.function.cdc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getindata.flink.sessionizer.model.cdc.OrderReturn;
import org.apache.flink.api.common.functions.MapFunction;

import java.time.Instant;

public class MapToOrderReturn implements MapFunction<String, OrderReturn> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OrderReturn map(String cdcJson) throws Exception {
        JsonNode rootNode = objectMapper.readTree(cdcJson);

        validate(rootNode);
        JsonNode record = rootNode.path("after");

        String orderId = record.path("order_id").asText();
        Instant returnTimestamp = Instant.parse(record.path("return_timestamp").asText());

        return new OrderReturn(orderId, returnTimestamp);
    }

    private void validate(JsonNode rootNode) throws IllegalArgumentException {
        if (!rootNode.get("before").isNull()) {
            throw new IllegalArgumentException("Updates are not supported");
        }
        if (rootNode.get("after").isNull()) {
            throw new IllegalArgumentException("Deletes are not supported");
        }
    }
}
