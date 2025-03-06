package ai.getindata.flink.sessionizer.sessionwindow;

import ai.getindata.flink.sessionizer.model.Event;
import ai.getindata.flink.sessionizer.model.event.PageView;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.MergingWindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SessionElementWindowAssigner extends MergingWindowAssigner<Event, SessionTimeWindow> {
    private final long sessionTimeout;
    private final TypeSerializer<SessionTimeWindow> serializer;

    public SessionElementWindowAssigner(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        serializer = Types.POJO(SessionTimeWindow.class).createSerializer(new ExecutionConfig());
    }

    @Override
    public void mergeWindows(Collection<SessionTimeWindow> windows, MergeCallback<SessionTimeWindow> callback) {
        List<SessionTimeWindow> sortedWindows = windows.stream().sorted(Comparator.comparingLong(SessionTimeWindow::getStart)).collect(Collectors.toList());
        List<Tuple2<SessionTimeWindow, List<SessionTimeWindow>>> merged = new ArrayList<>();
        Tuple2<SessionTimeWindow, List<SessionTimeWindow>> currentMerge = null;

        for (SessionTimeWindow candidate : sortedWindows) { // first iteration
            if (currentMerge == null) {
                List<SessionTimeWindow> sequence = new ArrayList<>();
                sequence.add(candidate);
                currentMerge = new Tuple2<>(candidate, sequence);
            } else if (currentMerge.f0.intersects(candidate)) { // windows are intersecting
                List<SessionTimeWindow> sequence = currentMerge.f1;
                sequence.add(candidate);
                SessionTimeWindow newWindow = currentMerge.f0.cover(candidate);
                log.trace("Windows are intersecting, merging {} with {}, result {}", currentMerge.f0, candidate, newWindow);
                currentMerge = new Tuple2<>(newWindow, sequence);
            } else { // close the window
                merged.add(currentMerge);
                List<SessionTimeWindow> sequence = new ArrayList<>();
                sequence.add(candidate);
                currentMerge = new Tuple2<>(candidate, sequence);
            }
        }

        if (currentMerge != null) {
            merged.add(currentMerge);
        }

        List<Tuple2<SessionTimeWindow, List<SessionTimeWindow>>> toMerge = merged.stream().filter(pair -> pair.f1.size() > 1).collect(Collectors.toList());
        log.debug("Merging windows: {} into pairs: {}", windows, toMerge);
        toMerge.forEach(pair -> callback.merge(pair.f1, pair.f0));
    }

    @Override
    public Collection<SessionTimeWindow> assignWindows(Event input, long timestamp, WindowAssignerContext context) {
        var event = input; // type casting to avoid type change of window which is incompatible change
        long evTimestamp = event.getTimestamp();
        List<SessionTimeWindow> sessionTimeWindows;
        if (event.getOrder() != null) {
            sessionTimeWindows = Collections.singletonList(new SessionTimeWindow(evTimestamp, evTimestamp + sessionTimeout, event.getKey(), true, null, event.getTimeZone()));
        } else {
            PageView pageview = event.getPageView();
            sessionTimeWindows = Collections.singletonList(new SessionTimeWindow(evTimestamp, evTimestamp + sessionTimeout, event.getKey(), false, pageview.getMarketingChannel(), "UTC"));
        }
        log.trace("Assigning window, timestamp {}, window {}", timestamp, sessionTimeWindows);
        return sessionTimeWindows;
    }

    @Override
    public Trigger<Event, SessionTimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
        return new OnEventFireTrigger<>();
    }

    @Override
    public TypeSerializer<SessionTimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
        return serializer;
    }

    @Override
    public boolean isEventTime() {
        return true;
    }
}
