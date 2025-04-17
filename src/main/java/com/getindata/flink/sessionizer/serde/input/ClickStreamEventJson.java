package com.getindata.flink.sessionizer.serde.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickStreamEventJson {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private PageView pageview;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Order order;
        private String key;
        private String type;
        private String frontendId;
        private String trackedBy;
        private String source;
        private String createdAt;
}
