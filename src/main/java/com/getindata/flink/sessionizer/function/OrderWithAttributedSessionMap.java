package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.UUID;

public class OrderWithAttributedSessionMap implements MapFunction<com.getindata.flink.sessionizer.model.OrderWithAttributedSessions, com.getindata.flink.sessionizer.serde.output.OrderWithAttributedSessions> {

    @Override
    public com.getindata.flink.sessionizer.serde.output.OrderWithAttributedSessions map(OrderWithAttributedSessions ows) throws Exception {
        String sessionId = ows.getSessions().isEmpty()
                ? UUID.randomUUID().toString()
                : ows.getSessions().stream().findFirst().get().getSession().getId();

        return new com.getindata.flink.sessionizer.serde.output.OrderWithAttributedSessions(
                ows.getOrder().getId(), sessionId, ows.getUserId().getValue(), ows.getTimestamp(), 0, 0
        );
    }
}
