package com.getindata.flink.sessionizer.model;

import com.getindata.flink.sessionizer.model.event.Order;
import com.getindata.flink.sessionizer.typing.SessionsTypeInfoFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithSessions {

    private Order order;

    @TypeInfo(SessionsTypeInfoFactory.class)
    private List<Session> sessions;
}
