package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.MarketingChannel;
import com.getindata.flink.sessionizer.model.event.Order;
import com.getindata.flink.sessionizer.model.event.PageView;
import com.getindata.flink.sessionizer.serde.input.ClickStreamEventJson;
import com.getindata.flink.sessionizer.util.HashingUtility;
import org.apache.flink.api.common.functions.MapFunction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MapToClickStreamEvent implements MapFunction<ClickStreamEventJson, ClickStreamEvent> {

    @Override
    public ClickStreamEvent map(ClickStreamEventJson input) {
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

        return new ClickStreamEvent(
                Instant.parse(input.createdAt()).toEpochMilli(),
                "UTC",
                new Key(input.key()),
                pageView,
                order
        );
    }
}
