package com.getindata.flink.sessionizer.model;

import com.getindata.flink.sessionizer.model.event.Order;
import com.getindata.flink.sessionizer.typing.AttributedSessionsTypeInfoFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithAttributedSessions {

    private Key userId;

    private long timestamp;

    private Order order;

    @TypeInfo(AttributedSessionsTypeInfoFactory.class)
    private List<AttributedSession> sessions;
}
