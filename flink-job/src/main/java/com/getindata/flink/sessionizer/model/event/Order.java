package com.getindata.flink.sessionizer.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String id;
    private long timestamp;
    private float total;
    private float shipping;
    private Long returnedTimestamp;
}
