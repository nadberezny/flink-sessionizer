package com.getindata.flink.sessionizer.sessionwindow;

import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import com.getindata.flink.sessionizer.typing.EventListTypeInfoFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionWindowAccumulator implements Serializable {
    // Should only keep first, secondLast and last ClickStreamEvent
    @TypeInfo(EventListTypeInfoFactory.class)
    private List<ClickStreamEvent> events = new ArrayList<>();
    private int pageViewsCount;

    public ClickStreamEvent firstEvent() {
        return events.get(0);
    }

    public ClickStreamEvent lastEvent() {
        return events.get(events.size() - 1) ;
    }

    public Optional<ClickStreamEvent> secondLastEvent() {
        return events.size() >= 2 ? Optional.ofNullable(events.get(events.size() - 2)) : Optional.empty();
    }
}
