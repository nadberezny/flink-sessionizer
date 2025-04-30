package com.getindata.sessionizer.datagen.producer;

import com.getindata.sessionizer.datagen.serde.kafka.Event;
import com.getindata.sessionizer.datagen.serde.kafka.EventKey;

public interface Producer {

    void send(EventKey eventKey, Event event);
}
