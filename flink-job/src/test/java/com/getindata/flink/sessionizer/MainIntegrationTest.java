package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.model.cdc.OrderReturn;
import com.getindata.flink.sessionizer.serde.input.ClickStreamEventJson;
import com.getindata.flink.sessionizer.serde.input.Order;
import com.getindata.flink.sessionizer.serde.input.PageView;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static com.getindata.flink.sessionizer.IntegrationTestExtension.clickStreamTopic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.given;


@ExtendWith(IntegrationTestExtension.class)
public class MainIntegrationTest {

    @Test
    void test(IntegrationTestExtension.IntegrationTextCtx ctx) throws Exception {
        // given
        var key1 = "key1";
        var user1 = "user1";
        var order1Id = "order1";
        var t1 = Instant.parse("2025-04-09T00:00:00Z");
        var t2 = Instant.parse("2025-04-09T00:05:00Z");
        var t3 = Instant.parse("2025-04-09T00:07:00Z");
        var t4 = Instant.parse("2025-04-09T01:00:00Z");
        var pv1 = new ClickStreamEventJson(
                new PageView(user1, null, "https://mystore.com", "ChannelA", null, "CampaignA", null, null, null, "ip1", null),
                null, key1, "pageview", "frontend1", "n/a", "n/a", t1.toString()
        );
        var pv2 = new ClickStreamEventJson(
                new PageView(user1, null, "https://mystore.com", "ChannelA", null, "CampaignA", null, null, null, "ip1", null),
                null, key1, "pageview", "frontend1", "n/a", "n/a", t2.toString()
        );
        var pv3 = new ClickStreamEventJson(
                new PageView(user1, null, "https://mystore.com", "ChannelA", null, "CampaignA", null, null, null, "ip1", null),
                null, key1, "pageview", "frontend1", "n/a", "n/a", t3.toString()
        );
        var order1 = new ClickStreamEventJson(null, new Order(user1, order1Id, null, null, null, BigInteger.valueOf(100), null, BigInteger.valueOf(20), null, null, null, null, null, null),
                key1, "order", "frontend1", "n/a", "n/a", t4.toString());

        // when
        ctx.getClickStreamProducer().send(
                new ProducerRecord<>(clickStreamTopic, pv1)
        );


        // then
        // pv1
        AtomicReference<String> sessionId = new AtomicReference<>();
        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.getSessionsConsumer().poll(Duration.ofMillis(500));
            assertThat(records).hasSize(1);
            records.forEach(record -> {
                assertThat(record.value().getCampaign()).isEqualTo("CampaignA");
                assertThat(record.value().getPageViewCount()).isEqualTo(1);
                sessionId.set(record.value().getSessionId());
            });
            return true;
        });


        ctx.getClickStreamProducer().send(
                new ProducerRecord<>(clickStreamTopic, pv2)
        );
        // pv2
        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.getSessionsConsumer().poll(Duration.ofMillis(500));
            assertThat(records).hasSize(1);
            records.forEach(record -> {
                assertThat(record.value().getCampaign()).isEqualTo("CampaignA");
                assertThat(record.value().getPageViewCount()).isEqualTo(1);
                assertThat(record.value().getDurationMillis()).isEqualTo(300000);
                assertThat(record.value().getSessionId()).isEqualTo(sessionId.get());
            });
            return true;
        });

        ctx.getClickStreamProducer().send(
                new ProducerRecord<>(clickStreamTopic, pv3)
        );
        // pv3
        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.getSessionsConsumer().poll(Duration.ofMillis(500));
            assertThat(records).hasSize(1);
            records.forEach(record -> {
                assertThat(record.value().getCampaign()).isEqualTo("CampaignA");
                assertThat(record.value().getPageViewCount()).isEqualTo(1);
                assertThat(record.value().getDurationMillis()).isEqualTo(120000);
                assertThat(record.value().getSessionId()).isEqualTo(sessionId.get());
            });
            return true;
        });

        ctx.getClickStreamProducer().send(
                new ProducerRecord<>(clickStreamTopic, order1)
        );

        // order1
        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.getOrderWithSessionsConsumer().poll(Duration.ofMillis(500));
            records.forEach(record -> {
                assertThat(record.value().getOrderId()).isEqualTo(order1Id);
                assertThat(record.value().getCampaign()).isEqualTo("CampaignA");
            });
            assertThat(records).hasSize(1);
            return true;
        });

        // CDC
        var order1ReturnTimestamp = Instant.parse("2025-04-10T01:00:00Z");
        ctx.getOrderReturnsRepository().insert(new OrderReturn(order1Id, order1ReturnTimestamp));
        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.getOrderWithSessionsConsumer().poll(Duration.ofMillis(500));
            assertThat(records).hasSize(1);
            records.forEach(record ->
                    assertThat(record.value().getReturnTimestamp()).isEqualTo(order1ReturnTimestamp.toEpochMilli())
            );
            return true;
        });
    }
}
