package com.getindata.flink.sessionizer.sessionwindow;

import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

public class OnEventFireTrigger<T, S extends Window>  extends Trigger<T, S> {

    @Override
    public TriggerResult onElement(T element, long timestamp, S window, TriggerContext ctx) throws Exception {
        return TriggerResult.FIRE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, S window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, S window, TriggerContext ctx) throws Exception {
        return time == window.maxTimestamp() ? TriggerResult.PURGE : TriggerResult.CONTINUE;
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public void clear(S window, TriggerContext ctx) throws Exception {
        ctx.deleteEventTimeTimer(window.maxTimestamp());
    }

    @Override
    public void onMerge(S window, OnMergeContext ctx) {
        // only register a timer if the watermark is not yet past the end of the merged window
        // this is in line with the logic in onElement(). If the watermark is past the end of
        // the window onElement() will fire and setting a timer here would fire the window twice.
        long timestamp = window.maxTimestamp();
        if (timestamp > ctx.getCurrentWatermark()) ctx.registerEventTimeTimer(timestamp);
    }

    @Override
    public String toString() {
        return "OnEveryElementTrigger()";
    }
}

