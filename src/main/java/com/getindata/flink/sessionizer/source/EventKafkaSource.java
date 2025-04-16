package com.getindata.flink.sessionizer.source;

import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.serde.kafka.EventDeserializationSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

import java.util.Properties;

public class EventKafkaSource {

    public static final String name = "events-source";

    public static KafkaSource<ClickStreamEvent> create(String bootstrapServers, Properties kafkaProperties, String topic, OffsetsInitializer offsetsInitializer) {
        return KafkaSource.<ClickStreamEvent>builder()
                .setBootstrapServers(bootstrapServers)
                .setGroupId(name)
                .setClientIdPrefix(topic)
                .setTopics(topic)
                .setStartingOffsets(offsetsInitializer)
                .setDeserializer(new EventDeserializationSchema())
                .setProperties(kafkaProperties)
                .build();
    }
}
