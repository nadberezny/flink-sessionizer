package com.getindata.flink.sessionizer.service;

import com.getindata.flink.sessionizer.model.AttributedSession;
import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.OrderWithSessions;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DummyAttributionService implements AttributionService {
    @Override
    public OrderWithAttributedSessions apply(OrderWithSessions orderWithSessions) {
        if (orderWithSessions.getSessions().isEmpty()) return orderWithAttributedSessions(orderWithSessions, List.of());
        var sessions = orderWithSessions.getSessions().stream().toList();
//        long dummyWeight = 1000000 / sessions.size();
        long dummyWeight = ThreadLocalRandom.current().nextLong(10, 101);
        var attributed = sessions.stream().map(session ->
                new AttributedSession(dummyWeight, session)
        ).toList();

        return orderWithAttributedSessions(orderWithSessions, attributed);
    }

    private OrderWithAttributedSessions orderWithAttributedSessions(OrderWithSessions orderWithSessions, List<AttributedSession> attributed) {
        return new OrderWithAttributedSessions(orderWithSessions.getUserId(), orderWithSessions.getTimestamp(), orderWithSessions.getOrder(), attributed);
    }

    @Override
    public void close() throws Exception {
        // no op
    }
}
