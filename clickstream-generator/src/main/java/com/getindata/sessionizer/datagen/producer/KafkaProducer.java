package com.getindata.sessionizer.datagen.producer;

import com.getindata.sessionizer.datagen.KafkaProducerFactory;
import com.getindata.sessionizer.datagen.conf.KafkaConfig;
import com.getindata.sessionizer.datagen.serde.kafka.Event;
import com.getindata.sessionizer.datagen.serde.kafka.EventKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;

public class KafkaProducer implements Producer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final KafkaConfig kafkaConfig;

    private final org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;

    public KafkaProducer(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        this.producer = KafkaProducerFactory.create(kafkaConfig);
    }

    @Override
    @SneakyThrows
    public void send(EventKey eventKey, Event event) {
        this.producer.send(getKafkaRecord(eventKey, event));
    }

    private ProducerRecord<String, String> getKafkaRecord(EventKey eventKey, Event event) throws JsonProcessingException {
        var keyJson = objectMapper.writeValueAsString(eventKey);
        var valueJson = objectMapper.writeValueAsString(event);
        return new ProducerRecord<>(kafkaConfig.getOutputTopic(), null, Instant.parse(event.createdAt()).toEpochMilli(), keyJson, valueJson);
    }
}
