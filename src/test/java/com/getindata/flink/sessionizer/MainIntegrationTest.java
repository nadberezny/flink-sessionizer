package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.serde.input.ClickStreamEventJson;
import com.getindata.flink.sessionizer.serde.input.Order;
import com.getindata.flink.sessionizer.serde.input.PageView;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;

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
        var t1 = Instant.parse("2025-04-09T00:00:00Z");
        var t2 = Instant.parse("2025-04-09T01:00:00Z");
        var pv1 = new ClickStreamEventJson(
                new PageView(user1, null, "https://mystore.com", "ChannelA", null, null, null, null, null, "ip1", null),
                null, key1, "pageview", "frontend1", "n/a", "n/a", t1.toString()
        );
//        var pv2 = new ClickStreamEvent(
//                new PageView(user1, null, null, null, null, null, null, null, null, null, null),
//                null, key1, null, null, null, null, t1.toString()
//        );
        var order1 = new ClickStreamEventJson(null, new Order(user1, "order1", null, null, null, BigInteger.valueOf(100), null, BigInteger.valueOf(20), null, null, null, null, null, null),
                key1, "order", "frontend1", "n/a", "n/a", t2.toString());

        // when
        ctx.getClickStreamProducer().send(
                new ProducerRecord<>(clickStreamTopic, pv1)
        );
        ctx.getClickStreamProducer().send(
                new ProducerRecord<>(clickStreamTopic, order1)
        );

        // then
        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.getSessionsConsumer().poll(Duration.ofMillis(500));
            assertThat(records).hasSize(1);
            return true;
        });

        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.getOrderWithSessionsConsumer().poll(Duration.ofMillis(500));
            assertThat(records).hasSize(1);
            return true;
        });
    }
}
