package ai.getindata.flink.sessionizer.model;

import ai.getindata.flink.sessionizer.model.event.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithSessions {

    private Order order;

    private List<Session> sessions;
}
