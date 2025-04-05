package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.AttributedSession;
import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.serde.output.AttributedOrderJson;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Optional;
import java.util.UUID;

public class MapToAttributedOrderJson implements MapFunction<OrderWithAttributedSessions, AttributedOrderJson> {

    @Override
    public AttributedOrderJson map(OrderWithAttributedSessions ows) throws Exception {
        Optional<Session> lastSession = getLastSession(ows);
        String sessionId = lastSession.map(Session::getId).orElse(UUID.randomUUID().toString());
        int pageViewCount = lastSession.map(Session::getPageViewCount).orElse(0);
        int durationMillis = lastSession.map(Session::getDurationMillis).orElse(0L).intValue();

        return new AttributedOrderJson(
                ows.getOrder().getId(), sessionId, ows.getUserId().getValue(), ows.getTimestamp(), pageViewCount, durationMillis
        );
    }

    private Optional<Session> getLastSession(OrderWithAttributedSessions ows) {
        return ows.getSessions().stream()
                .reduce((first, second) -> second) // Finds the last element
                .map(AttributedSession::getSession);
    }
}
