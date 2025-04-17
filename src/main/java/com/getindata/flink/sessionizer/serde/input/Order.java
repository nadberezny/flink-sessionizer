package com.getindata.flink.sessionizer.serde.input;

import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String uid;
    private String orderId;
    private String ip;
    private String country;
    private String userAgent;
    private BigInteger total;
    private BigInteger totalProductsNet;
    private BigInteger shipping;
    private String currency;
    private List<Product> products;
    private BigInteger tax;
    private List<Voucher> vouchers;
    private BigInteger paymentFee;
    private String paymentMethod;
}
