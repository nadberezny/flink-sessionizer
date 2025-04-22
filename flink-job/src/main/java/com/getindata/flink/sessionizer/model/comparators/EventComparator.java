package com.getindata.flink.sessionizer.model.comparators;

import com.getindata.flink.sessionizer.model.ClickStreamEvent;

import java.util.Comparator;

public class EventComparator implements Comparator<ClickStreamEvent> {
    @Override
    public int compare(ClickStreamEvent o1, ClickStreamEvent o2) {
        return Comparator.comparingLong(ClickStreamEvent::getTimestamp)
                .thenComparing(eventTypeComparator())
                .compare(o1, o2);
    }

    private Comparator<ClickStreamEvent> eventTypeComparator() {
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
