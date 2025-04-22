package com.getindata.flink.sessionizer.serde.input;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    private String voucher;
    private BigInteger voucherDiscount;
    private String voucherType;
}
