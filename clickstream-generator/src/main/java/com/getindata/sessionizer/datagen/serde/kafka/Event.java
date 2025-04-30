package com.getindata.sessionizer.datagen.serde.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;

public record Event(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        PageView pageview,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Order order,
        String key,
        String type,
        String frontendId,
        String trackedBy,
        String source,
        String createdAt) {
}
