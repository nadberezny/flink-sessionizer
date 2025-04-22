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
        long timestamp = Instant.parse(input.getCreatedAt()).toEpochMilli();

        PageView pageView;
        if (input.getPageview() != null) {
            var inputPv = input.getPageview();
            UUID id = HashingUtility.fromStrings(List.of(
                    inputPv.getUid(), inputPv.getUrl(), input.getCreatedAt()
            ));
            pageView = new PageView(
                    id.toString(),
                    new MarketingChannel(inputPv.getChannel()),
                    inputPv.getUrl());
        } else {
            pageView = null;
        }

        Order order;
        if (input.getOrder() != null) {
            order = new Order(
                    input.getOrder().getOrderId(),
                    timestamp,
                    input.getOrder().getTotal().floatValue(),
                    input.getOrder().getShipping().floatValue());
        } else {
            order = null;
        }

        return new ClickStreamEvent(
                Instant.parse(input.getCreatedAt()).toEpochMilli(),
                "UTC",
                new Key(input.getKey()),
                pageView,
                order
        );
    }
}
