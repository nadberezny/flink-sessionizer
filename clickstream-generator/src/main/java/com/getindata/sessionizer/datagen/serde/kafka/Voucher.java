package com.getindata.sessionizer.datagen.serde.kafka;

import java.math.BigInteger;

public record Voucher(String voucher,
                      BigInteger voucherDiscount,
                      String voucherType) {
}
