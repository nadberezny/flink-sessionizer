package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.OrderWithSessions;
import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.model.event.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

@Slf4j
public class SessionProcessor extends KeyedProcessFunction<Key, Session, OrderWithSessions> {

    private final Duration sessionStateTTL = Duration.ofDays(31);

    private final Duration maxSessionLookback = Duration.ofDays(30);

    public static final String SESSIONS_CACHE_STATE = "sessionsCacheState";

    // session.id -> session
    private static final MapStateDescriptor<String, Session> SESSIONS_CACHE_STATE_DESCRIPTOR = new MapStateDescriptor<>(SESSIONS_CACHE_STATE, String.class, Session.class);

    private MapState<String, Session> sessionsCacheState;

    @Override
    public void open(OpenContext openContext) throws Exception {
        super.open(openContext);
        SESSIONS_CACHE_STATE_DESCRIPTOR.enableTimeToLive(
                StateTtlConfig
                        .newBuilder(sessionStateTTL)
                        .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                        .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                        .build()
        );
        sessionsCacheState = getRuntimeContext().getMapState(SESSIONS_CACHE_STATE_DESCRIPTOR);
    }

    @Override
    public void processElement(Session session, KeyedProcessFunction<Key, Session, OrderWithSessions>.Context ctx, Collector<OrderWithSessions> out) throws Exception {
        if (session.isOrder()) {
            OrderWithSessions orderWithSessions = getOrderWithSession(ctx.getCurrentKey(), session.getOrder());
            out.collect(orderWithSessions);
        } else {
            add(session);
        }
    }

    private void addSessionToCache(Session session) throws Exception {
        String key = session.getId();
        Session mergedSession = Optional
                .ofNullable(sessionsCacheState.get(key))
                .map(toMerge -> toMerge.merge(session))
                .orElse(session);
        sessionsCacheState.put(key, mergedSession);
    }

    private Stream<Session> getSessionsFromCache() throws Exception {
        return stream(sessionsCacheState.entries().spliterator(), false)
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparingLong(Session::getTimestamp));
    }

    private void removeSessionsFromCache() throws Exception {
        if (sessionsCacheState != null) {
            List<String> keys = stream(sessionsCacheState.keys().spliterator(), false).collect(Collectors.toList());
            for (String k : keys) {
                sessionsCacheState.remove(k);
            }
        }
    }

    public OrderWithSessions getOrderWithSession(Key userId, Order order) throws Exception {
        log.info("Handling order event {}", order);
        List<Session> sessions = getSessionsByMaxLookback(order.getTimestamp() - maxSessionLookback.toMillis());
        removeSessionsFromCache();

        return new OrderWithSessions(userId, order.getTimestamp(), order, sessions);
    }

    public void add(Session session) throws Exception {
        log.debug("Handling session event {}", session);

        addSessionToCache(session);
        log.trace("Storing current session, sessionId: {}", session.getId());
    }

    private List<Session> getSessionsByMaxLookback(long maxSessionLookbackTimestamp) throws Exception {
        return getSessionsFromCache()
                .filter(s -> s.getTimestamp() >= maxSessionLookbackTimestamp)
                .collect(Collectors.toList());
    }
}
