package com.getindata.sessionizer.datagen.generators;

import com.getindata.sessionizer.datagen.serde.kafka.PageView;
import com.github.javafaker.Faker;

import java.util.List;
import java.util.UUID;

public class PageViewGenerator {

    private final List<String> availableUrls;

    public PageViewGenerator() {
        this.availableUrls = List.of(
            "https://example.com/home",
            "https://example.com/products",
            "https://example.com/categories",
            "https://example.com/cart",
            "https://example.com/checkout",
            "https://example.com/account",
            "https://example.com/about",
            "https://example.com/contact"
        );
    }

    public PageView generate(UUID visitorId, String productId, String channel, String channelGroup, 
                            String campaign, Faker faker) {
        int urlIndex = faker.random().nextInt(availableUrls.size());

        return new PageView(
                visitorId.toString(),
                productId,
                availableUrls.get(urlIndex),
                channel,
                channelGroup,
                campaign,
                faker.internet().url(),
                faker.country().countryCode2(),
                faker.internet().userAgentAny(),
                faker.internet().ipV4Address(),
                UUID.randomUUID().toString()
        );
    }
}
