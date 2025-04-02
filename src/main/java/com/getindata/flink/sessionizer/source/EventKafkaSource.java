package com.getindata.flink.sessionizer.source;

import com.getindata.flink.sessionizer.model.Event;
import com.getindata.flink.sessionizer.serde.kafka.EventDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class EventKafkaSource {

    public static final String name = "events-source";

    public static SingleOutputStreamOperator<Event> create(StreamExecutionEnvironment env, String bootstrapServers, String topic, int parallelism, OffsetsInitializer offsetsInitializer) {
        KafkaSource<Event> source = KafkaSource.<Event>builder()
                .setBootstrapServers(bootstrapServers)
                .setGroupId(name)
                .setClientIdPrefix(topic)
                .setTopics(topic)
                .setStartingOffsets(offsetsInitializer)
                .setDeserializer(new EventDeserializationSchema())
                .build();

        return env.fromSource(source, WatermarkStrategy.forMonotonousTimestamps(), name)
                .uid(name)
                .name(name)
                .setParallelism(parallelism);
    }
}
