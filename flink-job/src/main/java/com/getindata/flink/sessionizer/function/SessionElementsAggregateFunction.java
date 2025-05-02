package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.model.comparators.EventComparator;
import com.getindata.flink.sessionizer.model.event.PageView;
import com.getindata.flink.sessionizer.sessionwindow.SessionWindowAccumulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class SessionElementsAggregateFunction implements AggregateFunction<ClickStreamEvent, SessionWindowAccumulator, Session> {

    private static final Comparator<ClickStreamEvent> EVENT_COMPARATOR = new EventComparator();

    private final long sessionTimeout;

    @Override
    public SessionWindowAccumulator createAccumulator() {
        return new SessionWindowAccumulator();
    }

    @Override
    public SessionWindowAccumulator add(ClickStreamEvent event, SessionWindowAccumulator accumulator) {
        log.trace("Adding event {} to accumulator {}", event, accumulator);
        List<ClickStreamEvent> events = accumulator.getEvents();
        events.add(event);
        events = events.stream().sorted(EVENT_COMPARATOR).collect(Collectors.toList());
        if (events.size() > 3) {
            events.remove(1);
        }
        accumulator.setEvents(events);
        if (event.getPageView() != null) {
            accumulator.setPageViewsCount(accumulator.getPageViewsCount() + 1);
        }
        return accumulator;
    }

    @Override
    public Session getResult(SessionWindowAccumulator accumulator) {
        log.trace("Creating SessionEvent from accumulator {}", accumulator);
        try {
            ClickStreamEvent lastEvent = accumulator.lastEvent();
            Session.SessionBuilder sessionBuilder = Session.builder();
            sessionBuilder
                    .withTimestamp(lastEvent.getTimestamp())
                    .withLastEvent(lastEvent);

            if (lastEvent.getOrder() != null) {
                sessionBuilder
                        .withId(lastEvent.getOrder().getId())
                        .withUserId(lastEvent.getKey())
                        .withWindowFrom(lastEvent.getTimestamp())
                        .withWindowTo(lastEvent.getTimestamp());
            } else {
                PageView firstPageView = accumulator.firstEvent().getPageView();

                sessionBuilder
                        .withId(accumulator.firstEvent().getPageView().getId())
                        .withUserId(lastEvent.getKey())
                        .withWindowFrom(lastEvent.getTimestamp())
                        .withWindowTo(lastEvent.getTimestamp() + sessionTimeout)
                        .withPageViewCount(accumulator.getPageViewsCount())
                        .withDurationMillis(getDurationMillis(accumulator))
                        .withDurationMillisDelta(getDurationMillisDelta(accumulator))
                        .withMarketingChannel(firstPageView.getMarketingChannel())
                        .withCampaign(firstPageView.getCampaign())
                        .withLandingPage(firstPageView.getLandingPage());

            }
            return sessionBuilder.build();
        } catch (RuntimeException e) {
            log.error("Failed to create SessionJson out of {}", accumulator);
            throw e;
        }
    }

    @Override
    public SessionWindowAccumulator merge(SessionWindowAccumulator a, SessionWindowAccumulator b) {
        log.trace("merging window accumulators {} and {}", a, b);
        List<ClickStreamEvent> events = a.getEvents();
        events.addAll(b.getEvents());
        events = events.stream().sorted(EVENT_COMPARATOR).collect(Collectors.toList());

        if (events.size() > 3) {
            ArrayList<ClickStreamEvent> result = new ArrayList<>();
            result.add(events.get(0));
            result.addAll(events.subList(events.size() - 2, events.size()));
            a.setEvents(result);
        } else {
            a.setEvents(events);
        }
        a.setPageViewsCount(a.getPageViewsCount() + b.getPageViewsCount());
        return a;
    }

    private long getDurationMillis(SessionWindowAccumulator accumulator) {
        if (accumulator.firstEvent() != null) {
            return accumulator.lastEvent().getTimestamp() - accumulator.firstEvent().getTimestamp();
        }
        return 0;
    }

    private long getDurationMillisDelta(SessionWindowAccumulator accumulator) {
        if (accumulator.secondLastEvent().isPresent()) {
            return accumulator.lastEvent().getTimestamp() - accumulator.secondLastEvent().get().getTimestamp();
        }
        return 0;
    }
}
