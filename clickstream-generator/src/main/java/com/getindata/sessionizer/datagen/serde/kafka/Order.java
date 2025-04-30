package com.getindata.sessionizer.datagen.serde.kafka;

import java.math.BigInteger;
import java.util.List;

public record Order(String uid,
                    String orderId,
                    String ip,
                    String country,
                    String userAgent,
                    BigInteger total,
                    BigInteger totalProductsNet,
                    BigInteger shipping,
                    String currency,
                    List<Product> products,
                    BigInteger tax,
                    List<Voucher> vouchers,
                    BigInteger paymentFee,
                    String paymentMethod) {
}
