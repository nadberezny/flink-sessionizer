package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.config.JobConfig;
import com.getindata.flink.sessionizer.config.KafkaConfig;
import com.getindata.flink.sessionizer.function.MapToAttributedOrderJson;
import com.getindata.flink.sessionizer.function.MapToOrderWithAttributedSessions;
import com.getindata.flink.sessionizer.function.MapToSessionJson;
import com.getindata.flink.sessionizer.function.SessionElementsAggregateFunction;
import com.getindata.flink.sessionizer.function.SessionProcessor;
import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.OrderWithSessions;
import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.serde.output.AttributedOrderJson;
import com.getindata.flink.sessionizer.serde.output.SessionJson;
import com.getindata.flink.sessionizer.service.AttributionService;
import com.getindata.flink.sessionizer.service.DummyAttributionService;
import com.getindata.flink.sessionizer.sessionwindow.SessionElementWindowAssigner;
import com.getindata.flink.sessionizer.sink.KafkaJsonSinkFactory;
import com.getindata.flink.sessionizer.source.EventKafkaSource;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {

    public static void main(String[] args) throws Exception {
        var config = new JobConfig(ConfigFactory.load());
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        build(config, env, new DummyAttributionService());
        env.execute();
    }

    public static void build(JobConfig config, StreamExecutionEnvironment env, AttributionService attributionService) {
        // Source
        KafkaConfig kafkaConfig = config.getKafkaConfig();
        KafkaSource<ClickStreamEvent> clickStreamKafkaSource = EventKafkaSource.create(
                kafkaConfig.getBootstrapServers(), kafkaConfig.getKafkaProperties(), kafkaConfig.getClickStreamTopic(), OffsetsInitializer.earliest());

        // Building sessions
        DataStream<ClickStreamEvent> clickStreamEvents = env
                .fromSource(clickStreamKafkaSource, WatermarkStrategy.forMonotonousTimestamps(), "click-stream");

        DataStream<Session> sessions = clickStreamEvents
                .keyBy((KeySelector<ClickStreamEvent, Key>) ClickStreamEvent::getKey)
                .window(new SessionElementWindowAssigner(config.getSessionInactivityGap().toMillis()))
                .aggregate(new SessionElementsAggregateFunction(config.getSessionInactivityGap().toMillis()));

        DataStream<OrderWithSessions> orderWithSessions = sessions
                .keyBy(Session::getUserId)
                .process(new SessionProcessor());

        // Attribution
        DataStream<OrderWithAttributedSessions> attributedOrders = orderWithSessions
                .map(new MapToOrderWithAttributedSessions(() -> attributionService));

        // Sinks
        KafkaSink<SessionJson> sessionsSink = KafkaJsonSinkFactory.create(
                kafkaConfig.getBootstrapServers(),
                kafkaConfig.getKafkaProperties(),
                kafkaConfig.getSessionsTopic(),
                (KeySelector<SessionJson, String>) SessionJson::sessionId,
                SessionJson::timestamp);

        KafkaSink<AttributedOrderJson> attributedOrdersSink = KafkaJsonSinkFactory.create(
                kafkaConfig.getBootstrapServers(),
                kafkaConfig.getKafkaProperties(),
                kafkaConfig.getAttributedOrdersTopic(),
                (KeySelector<AttributedOrderJson, String>) AttributedOrderJson::orderId,
                AttributedOrderJson::timestamp);

        sessions
                .filter(session -> !session.isOrder())
                .map(new MapToSessionJson())
                .sinkTo(sessionsSink);

        attributedOrders
                .map(new MapToAttributedOrderJson())
                .sinkTo(attributedOrdersSink);
    }
}
