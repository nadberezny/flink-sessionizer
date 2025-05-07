package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.MarketingChannel;
import com.getindata.flink.sessionizer.model.event.Order;
import com.getindata.flink.sessionizer.model.event.PageView;
import com.getindata.flink.sessionizer.serde.input.ClickStreamEventJson;
import com.getindata.flink.sessionizer.util.HashingUtility;
import org.apache.flink.api.common.functions.MapFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MapToClickStreamEvent implements MapFunction<ClickStreamEventJson, ClickStreamEvent> {

    public static final BigDecimal monetaryFactor = BigDecimal.valueOf(10000);

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
                    inputPv.getCampaign(),
                    inputPv.getUrl());
        } else {
            pageView = null;
        }

        Order order;
        if (input.getOrder() != null) {
            order = new Order(
                    input.getOrder().getOrderId(),
                    timestamp,
                    toFloatMonetary(input.getOrder().getTotal()),
                    toFloatMonetary(input.getOrder().getShipping()),
                    null);
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
    
    private float toFloatMonetary(BigInteger value) {
        return new BigDecimal(value)
                .divide(monetaryFactor, 4, BigDecimal.ROUND_HALF_UP)
                .floatValue();
    }
}
