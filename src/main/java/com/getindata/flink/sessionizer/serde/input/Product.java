package com.getindata.flink.sessionizer.serde.input;

import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
        private String id;
        private String variantNo;
        private String productId;
        private String name;
        private BigInteger price;
        private String currency;
        private List<String> category;
        private Long quantity;
}
