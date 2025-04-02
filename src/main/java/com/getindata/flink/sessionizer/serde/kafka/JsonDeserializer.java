package com.getindata.flink.sessionizer.serde.kafka;

import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> targetType;

    public JsonDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    @SneakyThrows
    public T deserialize(String topic, byte[] data) {
        return objectMapper.readValue(data, targetType);
    }

    @Override
    public void close() {
    }
}
