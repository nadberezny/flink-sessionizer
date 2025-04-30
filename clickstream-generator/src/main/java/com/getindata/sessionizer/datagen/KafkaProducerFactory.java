package com.getindata.sessionizer.datagen;

import com.getindata.sessionizer.datagen.conf.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerFactory {

    public static KafkaProducer<String, String> create(KafkaConfig kafkaConfig) {
        var properties = kafkaConfig.getKafkaProperties();
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }
}
