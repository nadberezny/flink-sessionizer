package com.getindata.flink.sessionizer.model.comparators;

import com.getindata.flink.sessionizer.model.Event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event o1, Event o2) {
        return Comparator.comparingLong(Event::getTimestamp)
                .thenComparing(eventTypeComparator())
                .compare(o1, o2);
    }

    private Comparator<Event> eventTypeComparator() {
        return (o1, o2) -> {
            if (o1.getType().equals(o2.getType())) {
                return 0;
            }
            if (o1.getOrder() != null) {
                return 1;
            }
            return -1;
        };
    }
}
