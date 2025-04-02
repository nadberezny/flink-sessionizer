package com.getindata.flink.sessionizer.serde.input;

import java.math.BigInteger;

public record Voucher(String voucher,
                      BigInteger voucherDiscount,
                      String voucherType) {
}
