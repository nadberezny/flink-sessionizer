package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.functions.SessionElementsAggregateFunction;
import com.getindata.flink.sessionizer.functions.SessionProcessor;
import com.getindata.flink.sessionizer.model.Event;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.OrderWithSessions;
import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.sessionwindow.SessionElementWindowAssigner;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

public class Main {

    static final Duration sessionInactivityGap = Duration.ofMinutes(30);

    public static void main(String[] args) throws Exception {
        var env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Event> events = env.fromElements(new Event());

        SingleOutputStreamOperator<Session> sessions = events
                .keyBy((KeySelector<Event, Key>) Event::getKey)
                .window(new SessionElementWindowAssigner(sessionInactivityGap.toMillis()))
                .aggregate(new SessionElementsAggregateFunction(sessionInactivityGap.toMillis()));

        SingleOutputStreamOperator<Session> enrichedSessions = sessions
                .keyBy(Session::getKey)
                .process(new SessionProcessor());

        SideOutputDataStream<OrderWithSessions> orderWithSessions = enrichedSessions
                .getSideOutput(SessionProcessor.orderWithSessionsOutputTag);

        enrichedSessions.print();
        orderWithSessions.print();

        env.execute();
    }
}
