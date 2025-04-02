package com.getindata.flink.sessionizer.serde.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getindata.flink.sessionizer.function.InputToEventMap;
import com.getindata.flink.sessionizer.model.Event;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class EventDeserializationSchema implements KafkaRecordDeserializationSchema<Event> {

    private final InputToEventMap inputToEventMap = new InputToEventMap();

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<Event> collector) throws IOException {
        var input = new ObjectMapper()
                .readValue(consumerRecord.value(), com.getindata.flink.sessionizer.serde.input.Event.class);

        collector.collect(inputToEventMap.map(input));
    }

    @Override
    public TypeInformation<Event> getProducedType() {
        return Types.POJO(Event.class);
    }
}
