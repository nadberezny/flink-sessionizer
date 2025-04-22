package com.getindata.flink.sessionizer.function;

import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.serde.output.SessionJson;
import org.apache.flink.api.common.functions.MapFunction;

public class MapToSessionJson implements MapFunction<Session, SessionJson> {
    
    @Override
    public SessionJson map(Session session) throws Exception {
        session.getLastEvent().getPageView().getMarketingChannel();
        return new SessionJson(
                session.getId(),
                session.getUserId().getValue(),
                session.getMarketingChannel().getName(),
                session.getTimestamp(),
                session.getPageViewCount(),
                (int) session.getDurationMillis());
    }
}
