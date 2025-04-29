package com.getindata.flink.sessionizer.service;

import lombok.RequiredArgsConstructor;
import org.apache.flink.util.function.SerializableSupplier;

@RequiredArgsConstructor
public final class ExternalAttributionServiceSupplier implements SerializableSupplier<AttributionService> {

    private final String attributionServiceUrl;

    @Override
    public AttributionService get() {
        return new ExternalAttributionService(attributionServiceUrl);
    }
}
