package com.getindata.flink.sessionizer.serde.input;

public record EventKey(String frontendId, String visitorId) {
}
