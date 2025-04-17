package com.getindata.flink.sessionizer.serde.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventKey {
    private String frontendId;
    private String visitorId;
}
