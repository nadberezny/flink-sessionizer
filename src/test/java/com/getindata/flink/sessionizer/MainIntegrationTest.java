package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.model.Event;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.event.Order;
import com.getindata.flink.sessionizer.model.event.PageView;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.time.Instant;

import static com.getindata.flink.sessionizer.IntegrationTestExtension.inputTopic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.given;


@ExtendWith(IntegrationTestExtension.class)
public class MainIntegrationTest {

    @Test
    void test(IntegrationTestExtension.IntegrationTextCtx ctx) throws Exception {
        // given
        var t1 = Instant.parse("2025-04-09T00:00:00Z");
        var t2 = Instant.parse("2025-04-09T01:00:00Z");
        var pv1 = new Event(t1.toEpochMilli(), "UTC", new Key("k1"), new PageView(), null);
        var order1 = new Event(t2.toEpochMilli(), "UTC", new Key("k1"), null, new Order("order1", t2.toEpochMilli()));

        // when
        ctx.eventProducer().send(
                new ProducerRecord<>(inputTopic, pv1)
        );
        ctx.eventProducer().send(
                new ProducerRecord<>(inputTopic, order1)
        );

        // then
        given().ignoreExceptions().atMost(Duration.ofMinutes(3)).await().until(() -> {
            var records = ctx.orderWithSessionsConsumer().poll(Duration.ofMillis(500));
            assertThat(records).hasSize(1);
            return true;
        });
    }
}
