package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.config.CDCConfig;
import com.getindata.flink.sessionizer.config.JobConfig;
import com.getindata.flink.sessionizer.config.KafkaConfig;
import com.getindata.flink.sessionizer.function.MapToAttributedOrderJson;
import com.getindata.flink.sessionizer.function.MapToOrderWithAttributedSessions;
import com.getindata.flink.sessionizer.function.MapToSessionJson;
import com.getindata.flink.sessionizer.function.SessionElementsAggregateFunction;
import com.getindata.flink.sessionizer.function.SessionProcessor;
import com.getindata.flink.sessionizer.function.cdc.AttributedOrderCoProcessor;
import com.getindata.flink.sessionizer.function.cdc.MapToOrderReturn;
import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.OrderWithSessions;
import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.model.cdc.OrderReturn;
import com.getindata.flink.sessionizer.serde.output.AttributedOrderJson;
import com.getindata.flink.sessionizer.serde.output.SessionJson;
import com.getindata.flink.sessionizer.service.AttributionService;
import com.getindata.flink.sessionizer.service.ExternalAttributionServiceSupplier;
import com.getindata.flink.sessionizer.sessionwindow.SessionElementWindowAssigner;
import com.getindata.flink.sessionizer.sink.KafkaJsonSinkFactory;
import com.getindata.flink.sessionizer.source.EventKafkaSource;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cdc.connectors.mysql.source.MySqlSource;
import org.apache.flink.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.function.SerializableSupplier;

import static com.getindata.flink.sessionizer.commons.Commons.TIME_ZONE;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        JobConfig config = new JobConfig(ConfigFactory.load());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        buildTopology(config, env, new ExternalAttributionServiceSupplier(config.getAttributionServiceUrl()));
        env.execute();
    }

    public static void buildTopology(JobConfig config, StreamExecutionEnvironment env, SerializableSupplier<AttributionService> attributionServiceSupplier) {
        // Source
        KafkaConfig kafkaConfig = config.getKafkaConfig();
        log.info("Kafka config: {}", kafkaConfig);
        log.info("Kafka properties: {}", kafkaConfig.getKafkaProperties());
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
                .map(new MapToOrderWithAttributedSessions(attributionServiceSupplier));

        // Sinks
        KafkaSink<SessionJson> sessionsSink = KafkaJsonSinkFactory.create(
                kafkaConfig.getBootstrapServers(),
                kafkaConfig.getKafkaProperties(),
                kafkaConfig.getSessionsTopic(),
                (KeySelector<SessionJson, String>) SessionJson::getSessionId,
                SessionJson::getTimestamp);

        KafkaSink<AttributedOrderJson> attributedOrdersSink = KafkaJsonSinkFactory.create(
                kafkaConfig.getBootstrapServers(),
                kafkaConfig.getKafkaProperties(),
                kafkaConfig.getAttributedOrdersTopic(),
                (KeySelector<AttributedOrderJson, String>) AttributedOrderJson::getOrderId,
                AttributedOrderJson::getTimestamp);

        sessions
                .filter(session -> !session.isOrder())
                .map(new MapToSessionJson())
                .sinkTo(sessionsSink);

        attributedOrders
                .map(new MapToAttributedOrderJson())
                .sinkTo(attributedOrdersSink);

        // CDC
        if (config.getCdcConfig().isEnabled()) {
            buildCdcTopology(config.getCdcConfig(), env, attributedOrders, attributedOrdersSink);
        }
    }

    private static void buildCdcTopology(CDCConfig cdcConfig, StreamExecutionEnvironment env, DataStream<OrderWithAttributedSessions> attributedOrders, KafkaSink<AttributedOrderJson> attributedOrdersSink) {
        MySqlSource<String> cdcSource = MySqlSource.<String>builder()
                .hostname(cdcConfig.getHostname())
                .port(cdcConfig.getPort())
                .databaseList(cdcConfig.getDatabase())
                .tableList(cdcConfig.getTable())
                .username(cdcConfig.getUsername())
                .password(cdcConfig.getPassword())
                .serverTimeZone(TIME_ZONE.getID())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        DataStream<OrderReturn> cdcStream = env
                .fromSource(cdcSource, WatermarkStrategy.noWatermarks(), "cdc-source")
                .map(new MapToOrderReturn());

        DataStream<OrderWithAttributedSessions> returnedAttributedOrder = attributedOrders
                .connect(cdcStream)
                .keyBy(ows -> ows.getOrder().getId(), OrderReturn::getOrderId)
                .process(new AttributedOrderCoProcessor(cdcConfig.getStateTTL()));

        returnedAttributedOrder
                .map(new MapToAttributedOrderJson())
                .sinkTo(attributedOrdersSink);
    }
}
