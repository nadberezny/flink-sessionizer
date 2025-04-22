package com.getindata.flink.sessionizer.serde.kafka;

import com.getindata.flink.sessionizer.serde.output.SessionJson;

public class SessionsDeserializer extends JsonDeserializer<SessionJson> {

    public SessionsDeserializer() {
        super(SessionJson.class);
    }
}
