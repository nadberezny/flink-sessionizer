package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.OrderWithSessions;
import com.getindata.flink.sessionizer.service.AttributionService;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.util.function.SerializableSupplier;

public class MapToOrderWithAttributedSessions extends RichMapFunction<OrderWithSessions, OrderWithAttributedSessions> {

    private final SerializableSupplier<AttributionService> attributionServiceSupplier;

    private transient AttributionService attributionService;

    public MapToOrderWithAttributedSessions(SerializableSupplier<AttributionService> attributionServiceSupplier) {
        this.attributionServiceSupplier = attributionServiceSupplier;
    }

    @Override
    public void open(OpenContext openContext) throws Exception {
        super.open(openContext);
        attributionService = attributionServiceSupplier.get();
    }

    @Override
    public void close() throws Exception {
        if (attributionService != null) {
            attributionService.close();
        }
        super.close();
    }

    @Override
    public OrderWithAttributedSessions map(OrderWithSessions orderWithSessions) throws Exception {
        return attributionService.apply(orderWithSessions);
    }
}
