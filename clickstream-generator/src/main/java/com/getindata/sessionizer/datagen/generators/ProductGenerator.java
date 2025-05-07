package com.getindata.sessionizer.datagen.generators;

import com.getindata.sessionizer.datagen.commons.Commons;
import com.getindata.sessionizer.datagen.serde.kafka.Product;
import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ProductGenerator {

    public static Product generate(Faker faker, String[] availableProductNames, String currency) {
        String productName = faker.options().option(availableProductNames);
        String color = faker.commerce().color();
        String id = productName + ":" + color;
        BigInteger price = new BigDecimal(faker.commerce().price(50, 150)).multiply(Commons.monetaryFactor).toBigInteger();
        Product product = new Product(
                id,
                id,
                id,
                productName,
                price,
                currency,
                List.of(),
                1L
        );
        return product;
    }
}
