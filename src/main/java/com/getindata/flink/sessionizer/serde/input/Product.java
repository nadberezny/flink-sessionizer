package com.getindata.flink.sessionizer.serde.input;

import java.math.BigInteger;
import java.util.List;

public record Product(
        String id,
        String variantNo,
        String productId,
        String name,
        BigInteger price,
        String currency,
        List<String> category,
        Long quantity) {
}
