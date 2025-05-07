package com.getindata.sessionizer.datagen;

import com.getindata.sessionizer.datagen.generators.OrderGenerator;
import com.getindata.sessionizer.datagen.generators.PageViewGenerator;
import com.getindata.sessionizer.datagen.serde.kafka.Event;
import com.getindata.sessionizer.datagen.serde.kafka.EventKey;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.slf4j.event.Level;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.getindata.sessionizer.datagen.Main.appConfig;
import static com.getindata.sessionizer.datagen.Main.producer;

@Slf4j
class User implements Runnable {
    public static final List<String> availableUnknownCampaigns = List.of();

    private final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();

    private final UUID visitorId;

    private final EventKey eventKey;

    private final String id;

    private final UserBucket bucket;

    private final boolean isDryRun;

    private final Level logLevel;

    private boolean isInterrupted = false;

    private long sessionDurationLeftMs;

    private OrderGenerator orderGenerator;
    private PageViewGenerator pageViewGenerator;

    public User(String id, UserBucket bucket, boolean isDryRun, OrderGenerator orderGenerator) {
        this.id = id;
        this.bucket = bucket;
        this.isDryRun = isDryRun;
        this.logLevel = isDryRun ? Level.INFO : Level.DEBUG;
        this.sessionDurationLeftMs = calculateSessionDuration().toMillis();
        this.visitorId = UUID.randomUUID();
        this.eventKey = new EventKey(appConfig.frontendId, visitorId.toString());
        this.orderGenerator = orderGenerator;
        this.pageViewGenerator = new PageViewGenerator();
    }

    @SneakyThrows // TODO
    @Override
    public void run() {
        while (sessionDurationLeftMs >= 0 && !isInterrupted) {
            long pageViewTime = bucket.getPageViewInterval().toMillis();
            sessionDurationLeftMs = sessionDurationLeftMs - pageViewTime;

            boolean isLastAction = sessionDurationLeftMs <= bucket.getPageViewInterval().toMillis();
            Event event = isLastAction && shouldPlaceOrder() ? generateOrderEvent() : generatePageViewEvent();
            if (!isDryRun) {
                producer.send(eventKey, event);
            }

            if (pageViewTime > 0) {
                try {
                    Thread.sleep(pageViewTime + rng.nextInt(0, 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    isInterrupted = true;
                }
            }
        }

        log.debug("Done {}", id);
        bucket.decrement();
    }

    private Duration calculateSessionDuration() {
        int min = (int) bucket.getMinSession().toMinutes();
        int max = (int) bucket.getMaxSession().toMinutes();

        if (max == min) {
            return Duration.ofMinutes(max);
        }

        long durationMin = rng.nextInt(min, max + 1);
        return Duration.ofMinutes(durationMin);
    }

    private boolean shouldPlaceOrder() {
        int diceRollsLeft = bucket.getChanceForPlacingOrder();
        boolean shouldPlaceAnOrder = false;

        while (!shouldPlaceAnOrder && diceRollsLeft > 0) {
            diceRollsLeft--;
            int rnd = rng.nextInt(1, 100 + 1);
            if (rnd % 100 == 0) {
                shouldPlaceAnOrder = true;
            }
        }

        return shouldPlaceAnOrder;
    }

    private Event generateOrderEvent() {
        log.atLevel(logLevel).log("Sending order {}", id);
        var createdAt = Instant.now().toString();
        var orderId = id + createdAt;
        var faker = new Faker(new Random(visitorId.getMostSignificantBits()));
        var order = orderGenerator.generate(visitorId, orderId, "PLN", faker);
        return new Event(null, order, visitorId.toString(), "pageview", eventKey.frontendId(), "trackedBy", "source", createdAt);
    }

    @SneakyThrows
    private Event generatePageViewEvent() {
        log.atLevel(logLevel).log("Sending pageview {}", id);
        var now = Instant.now();
        var createdAt = now.toString();

        String channel = generateChannel();
        String campaign = generateCampaign(channel);
        var faker = new Faker(new Random(visitorId.getMostSignificantBits()));

        var pageView = pageViewGenerator.generate(visitorId, null, channel, null, campaign, faker);

        return new Event(pageView, null, visitorId.toString(), "pageview", eventKey.frontendId(), "trackedBy", "source", createdAt);
    }

    private String generateChannel() {
        int randomValue = rng.nextInt(1, 101);

        if (randomValue <= 30) {
            return "Facebook";
        } else if (randomValue <= 85) {
            return "Google";
        } else {
            return "Unknown";  // 15% probability
        }
    }

    private String generateCampaign(String channel) {
        switch (channel) {
            case "Facebook":
                return generateFacebookCampaign();
            case "Google":
                return generateGoogleCampaign();
            case "Unknown":
                if (availableUnknownCampaigns.isEmpty()) {
                    return null;
                }
                int index = rng.nextInt(availableUnknownCampaigns.size());
                return availableUnknownCampaigns.get(index);
            default:
                return null;
        }
    }

    private String generateFacebookCampaign() {
        int randomValue = rng.nextInt(1, 101);

        if (randomValue <= 10) {
            return "Flash Frenzy Weekend";  // 10% probability
        } else {
            return "Cart Buster Deals";  // 90% probability
        }
    }

    private String generateGoogleCampaign() {
        int randomValue = rng.nextInt(1, 101);

        if (randomValue <= 20) {
            return "New Year, New Finds";  // 20% probability
        } else if (randomValue <= 50) {
            return "12 Days of Savings";  // 30% probability
        } else {
            return "Summer Style Drop";  // 50% probability
        }
    }
}
