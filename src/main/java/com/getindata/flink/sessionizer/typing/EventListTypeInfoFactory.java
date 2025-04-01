package com.getindata.flink.sessionizer.typing;

import com.getindata.flink.sessionizer.model.Event;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

public class EventListTypeInfoFactory extends ListTypeInfoFactory<Event> {
    @Override
    protected TypeInformation<Event> elementsType() {
        return Types.POJO(Event.class);
    }
}
