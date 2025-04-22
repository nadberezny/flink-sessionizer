package com.getindata.flink.sessionizer.sessionwindow;

import com.getindata.flink.sessionizer.model.MarketingChannel;
import com.getindata.flink.sessionizer.model.Key;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.windowing.windows.Window;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.TimeZone;


@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class SessionTimeWindow extends Window {

    public static final SessionTimeWindow ZERO = new SessionTimeWindow(0L, 0L, new Key(), false, null, null);

    private long start;
    private long end;
    private Key key;
    private boolean closed;
    private MarketingChannel marketingChannel;

    private String timeZone;

    public SessionTimeWindow cover(SessionTimeWindow other) {
        MarketingChannel selectedMarketingChannel = this.marketingChannel == null ? other.marketingChannel : this.marketingChannel;
        // We can just use other.isClosed as closed windows are terminal and won't be merged with anything else
        return new SessionTimeWindow(Math.min(this.getStart(), other.getStart()), Math.max(this.getEnd(), other.getEnd()), other.key, other.closed, selectedMarketingChannel, this.timeZone);
    }

    public boolean intersects(SessionTimeWindow other) {
        // different channels don't intersect (only valid for pageviews)
        if (!other.isOrder() && !this.isOrder() && !this.marketingChannel.isIntersecting(other.getMarketingChannel())) {
            return false;
        }

        if (isAfterMidnight(other)) {
            return false;
        }

        return !closed && this.start <= other.end && this.end >= other.start;
    }

    private boolean isOrder() {
        return this.marketingChannel == null && this.closed;
    }

    private boolean isAfterMidnight(SessionTimeWindow other) {
        LocalDateTime timestampMidnight = LocalDateTime.of(LocalDate.ofInstant(Instant.ofEpochMilli(Math.min(this.getStart(), other.getStart())), TimeZone.getTimeZone(this.timeZone).toZoneId()), LocalTime.MIDNIGHT);
        LocalDateTime timestampMidnightDayAfter = timestampMidnight.plusDays(1);
        return Instant.ofEpochMilli(Math.max(this.getStart(), other.getStart())).isAfter(timestampMidnightDayAfter.toInstant(ZoneOffset.UTC));
    }

    @Override
    public long maxTimestamp() {
        return end - 1;
    }
}
