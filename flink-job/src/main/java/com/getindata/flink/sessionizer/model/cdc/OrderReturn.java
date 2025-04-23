package com.getindata.flink.sessionizer.model.cdc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReturn {

    private String orderId;
    private Instant returnTimestamp;
}
