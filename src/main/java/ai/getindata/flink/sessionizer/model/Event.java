package ai.getindata.flink.sessionizer.model;

import ai.getindata.flink.sessionizer.model.event.Order;
import ai.getindata.flink.sessionizer.model.event.PageView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Event {

    private long timestamp;

    private String timeZone;

    private Key key;

    private PageView pageView;

    private Order order;

    public String getType() {
        if (pageView != null) {
            return "pageView";
        } else if (order != null) {
            return "order";
        } else {
            return "unknown";
        }
    }
}
