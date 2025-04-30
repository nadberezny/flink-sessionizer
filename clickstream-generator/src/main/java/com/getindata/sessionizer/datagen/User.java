package com.getindata.sessionizer.datagen;

import com.getindata.sessionizer.datagen.serde.kafka.Event;
import com.getindata.sessionizer.datagen.serde.kafka.EventKey;
import com.getindata.sessionizer.datagen.serde.kafka.Order;
import com.getindata.sessionizer.datagen.serde.kafka.PageView;
import com.getindata.sessionizer.datagen.serde.kafka.Product;
import com.getindata.sessionizer.datagen.serde.kafka.Voucher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.slf4j.event.Level;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.getindata.sessionizer.datagen.Main.appConfig;
import static com.getindata.sessionizer.datagen.Main.producer;

@Slf4j
class User implements Runnable {

    public static final List<String> availableChannels = List.of("Facebook", "Google", "Unknown", "CampaignA", "CampaignB");

    private final int channelsSize = availableChannels.size();

    private final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();

    private final UUID visitorId;

    private final EventKey eventKey;

    private final String id;

    private final UserBucket bucket;

    private final boolean isDryRun;

    private final Level logLevel;

    private boolean isInterrupted = false;

    private long sessionDurationLeftMs;

    public User(String id, UserBucket bucket, boolean isDryRun) {
        this.id = id;
        this.bucket = bucket;
        this.isDryRun = isDryRun;
        this.logLevel = isDryRun ? Level.INFO : Level.DEBUG;
        this.sessionDurationLeftMs = calculateSessionDuration().toMillis();
        this.visitorId = UUID.randomUUID();
        this.eventKey = new EventKey(appConfig.frontendId, visitorId.toString());
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
        var TODO = new BigInteger("1");
        List<Product> products = List.of(
                new Product("id", "variantNo", "productId", "productName", TODO, "SEK", List.of(), 1L)
        );
        List<Voucher> vouchers = List.of();
        var order = new Order(visitorId.toString(), orderId, "ip", "PL", "userAgent", TODO, TODO, TODO, "PLN", products, TODO, vouchers, TODO, "paymentMethod");
        return new Event(null, order, visitorId.toString(), "pageview", eventKey.frontendId(), "trackedBy", "source", createdAt);
    }

    @SneakyThrows // TODO
    private Event generatePageViewEvent() {
        log.atLevel(logLevel).log("Sending pageview {}", id);
        var now = Instant.now();
        var createdAt = now.toString();
        var pageView = new PageView(
                visitorId.toString(), null, "https://loadtest.se", generateChannel(now.toEpochMilli()), null, null, null, "PL", "userAgent", "ip", null
        );

        return new Event(pageView, null, visitorId.toString(), "pageview", eventKey.frontendId(), "trackedBy", "source", createdAt);
    }

    private String generateChannel(long valueUnderModulo) {
        int index = (int) (valueUnderModulo % channelsSize);
        return availableChannels.get(index);
    }
}
