package com.getindata.flink.sessionizer.typing;

import com.getindata.flink.sessionizer.model.ClickStreamEvent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

public class EventListTypeInfoFactory extends ListTypeInfoFactory<ClickStreamEvent> {
    @Override
    protected TypeInformation<ClickStreamEvent> elementsType() {
        return Types.POJO(ClickStreamEvent.class);
    }
}
