package com.getindata.flink.sessionizer.sink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.util.function.SerializableFunction;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;

public class KafkaJsonSinkFactory {
    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .configure(READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
            .build();

    public static <V> KafkaSink<V> create(
            String bootstrapServers,
            String topic,
            KeySelector<V, String> keySelector,
            SerializableFunction<V, Long> timestampProvider) {
        return KafkaSink.<V>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(new SerializationSchema<>(topic, keySelector, timestampProvider))
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    static class SerializationSchema<V> implements KafkaRecordSerializationSchema<V>, Serializable {

        private final KeySelector<V, String> keySelector;
        private final String topic;
        private final SerializableFunction<V, Long> timestampProvider;

        SerializationSchema(String topic, KeySelector<V, String> keySelector, SerializableFunction<V, Long> timestampProvider) {
            this.topic = topic;
            this.keySelector = keySelector;
            this.timestampProvider = timestampProvider;
        }

        @Override
        public ProducerRecord<byte[], byte[]> serialize(V element, KafkaSinkContext context, Long ignoredTimestamp) {
            try {
                return new ProducerRecord<>(
                        topic,
                        null,
                        timestampProvider.apply(element),
                        keySelector.getKey(element).getBytes(StandardCharsets.UTF_8),
                        MAPPER.writeValueAsBytes(element)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
