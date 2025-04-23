package com.getindata.flink.sessionizer.function.cdc;

import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.cdc.OrderReturn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class AttributedOrderCoProcessor extends KeyedCoProcessFunction<String, OrderWithAttributedSessions, OrderReturn, OrderWithAttributedSessions> {

    private final Duration stateTTL;

    private static final ValueStateDescriptor<OrderWithAttributedSessions> ATTRIBUTED_ORDER_STATE =
            new ValueStateDescriptor<>("attributed-order-state", OrderWithAttributedSessions.class);

    private ValueState<OrderWithAttributedSessions> attributedOrderState;

    @Override
    public void open(Configuration parameters) {
        StateTtlConfig stateTTLConfig = StateTtlConfig.newBuilder(stateTTL)
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                .build();
        ATTRIBUTED_ORDER_STATE.enableTimeToLive(stateTTLConfig);
        attributedOrderState = getRuntimeContext().getState(ATTRIBUTED_ORDER_STATE);
    }

    @Override
    public void processElement1(OrderWithAttributedSessions orderWithAttributedSessions, KeyedCoProcessFunction<String, OrderWithAttributedSessions, OrderReturn, OrderWithAttributedSessions>.Context context, Collector<OrderWithAttributedSessions> collector) throws Exception {
        attributedOrderState.update(orderWithAttributedSessions);
    }

    @Override
    public void processElement2(OrderReturn orderReturn, KeyedCoProcessFunction<String, OrderWithAttributedSessions, OrderReturn, OrderWithAttributedSessions>.Context context, Collector<OrderWithAttributedSessions> collector) throws Exception {
        log.debug("Handling order return event {}", orderReturn);
        if (attributedOrderState.value() != null) {
            OrderWithAttributedSessions attributedOrder = attributedOrderState.value();
            attributedOrder.getOrder()
                    .setReturnedTimestamp(orderReturn.getReturnTimestamp().toEpochMilli());
            collector.collect(attributedOrder);
            attributedOrderState.clear();

        } else {
            log.warn("Received order return event for non-attributed order {}", orderReturn.getOrderId());
        }
    }
}
