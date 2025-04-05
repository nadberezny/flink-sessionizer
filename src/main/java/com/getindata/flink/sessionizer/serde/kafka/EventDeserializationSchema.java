package com.getindata.flink.sessionizer.serde.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getindata.flink.sessionizer.function.MapToClickStreamEvent;
import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.serde.input.ClickStreamEventJson;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class EventDeserializationSchema implements KafkaRecordDeserializationSchema<ClickStreamEvent> {

    private final MapToClickStreamEvent mapToClickStreamEvent = new MapToClickStreamEvent();

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<ClickStreamEvent> collector) throws IOException {
        var input = new ObjectMapper()
                .readValue(consumerRecord.value(), ClickStreamEventJson.class);

        collector.collect(mapToClickStreamEvent.map(input));
    }

    @Override
    public TypeInformation<ClickStreamEvent> getProducedType() {
        return Types.POJO(ClickStreamEvent.class);
    }
}
