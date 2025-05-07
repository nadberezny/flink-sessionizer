package com.getindata.sessionizer.datagen.generators;

import com.getindata.sessionizer.datagen.serde.kafka.Order;
import com.getindata.sessionizer.datagen.serde.kafka.Product;
import com.github.javafaker.Faker;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class OrderGenerator {

    String[] availableProductNames;

    public OrderGenerator() {
        Faker faker = new Faker();
        int maxProductNumber = 50;
        HashSet<String> distinctProductNames = new HashSet<>();
        IntStream.range(0, maxProductNumber).forEach(i -> {
            distinctProductNames.add(faker.commerce().productName());
        });
        this.availableProductNames = distinctProductNames.toArray(new String[0]);
    }

    public Order generate(UUID visitorId, String orderId, String currency, Faker faker) {
        Product product = ProductGenerator.generate(faker, availableProductNames, currency);
        return new Order(
                visitorId.toString(),
                orderId,
                faker.internet().ipV4Address(),
                faker.country().countryCode2(),
                faker.internet().userAgentAny(),
                product.price(),
                product.price(),
                BigInteger.ZERO,
                faker.currency().code(),
                List.of(product),
                BigInteger.ZERO,
                List.of(),
                BigInteger.ZERO,
                "paymentMethod"
        );
    }
}
