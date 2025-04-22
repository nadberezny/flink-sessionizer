package com.getindata.flink.sessionizer.service;

import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.OrderWithSessions;

import java.io.Serializable;

public interface AttributionService extends Serializable, AutoCloseable {

    OrderWithAttributedSessions apply(OrderWithSessions orderWithSessions);
}
