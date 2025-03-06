package ai.getindata.flink.sessionizer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public final class Session {

    private String id;
    private long timestamp;
    private Key key;
    private long windowFrom;
    private long windowTo;
    private int pageViewCount;
    private long durationMillis;
    private MarketingChannel marketingChannel;
    private String landingPage;
    private Event lastEvent;

    public Session merge(Session other) {
        this.durationMillis = (this.durationMillis + other.durationMillis);
        this.pageViewCount = (this.pageViewCount + other.pageViewCount);
        return this;
    }
}
