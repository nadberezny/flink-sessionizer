package com.getindata.flink.sessionizer.sink;

import com.getindata.flink.sessionizer.model.OrderWithSessions;
import com.getindata.flink.sessionizer.serde.kafka.OrderWithSessionsSerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;

public class OrderWithSessionsKafkaSink {

    public static KafkaSink<OrderWithSessions> create(String bootstrapServers, String topic) {
        KafkaRecordSerializationSchema<OrderWithSessions> kafkaRecordSerializationSchema = KafkaRecordSerializationSchema.<OrderWithSessions>builder()
                .setValueSerializationSchema(new OrderWithSessionsSerializationSchema())
                .setTopic(topic)
                .build();

        return KafkaSink.<OrderWithSessions>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(
                        kafkaRecordSerializationSchema
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
