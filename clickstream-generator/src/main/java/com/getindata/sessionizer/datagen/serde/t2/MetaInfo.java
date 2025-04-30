package com.getindata.sessionizer.datagen.serde.t2;

import com.getindata.sessionizer.datagen.serde.kafka.Order;
import com.getindata.sessionizer.datagen.serde.kafka.PageView;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with", toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaInfo {
    private Order order;
    private PageView pageview;
    private String key;
    private String createdAt;
    private String source;
    private String trackedBy;
    private String frontendId;
    private String type;
}
