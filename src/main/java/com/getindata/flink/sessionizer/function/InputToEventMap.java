package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.Event;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.MarketingChannel;
import com.getindata.flink.sessionizer.model.event.Order;
import com.getindata.flink.sessionizer.model.event.PageView;
import com.getindata.flink.sessionizer.util.HashingUtility;
import org.apache.flink.api.common.functions.MapFunction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class InputToEventMap implements MapFunction<com.getindata.flink.sessionizer.serde.input.Event, Event> {

    @Override
    public Event map(com.getindata.flink.sessionizer.serde.input.Event input) {
        long timestamp = Instant.parse(input.createdAt()).toEpochMilli();

        PageView pageView;
        if (input.pageview() != null) {
            var inputPv = input.pageview();
            UUID id = HashingUtility.fromStrings(List.of(
                   inputPv.uid(), inputPv.url(), input.createdAt()
            ));
            pageView = new PageView(
                    id.toString(),
                    new MarketingChannel(inputPv.channel()),
                    inputPv.url());
        } else {
            pageView = null;
        }

        Order order;
        if (input.order() != null) {
            order = new Order(input.order().orderId(), timestamp);
        } else {
            order = null;
        }

        return new Event(
                Instant.parse(input.createdAt()).toEpochMilli(),
                "UTC",
                new Key(input.key()),
                pageView,
                order
        );
    }
}
