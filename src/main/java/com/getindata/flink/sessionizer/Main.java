package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.config.JobConfig;
import com.getindata.flink.sessionizer.function.SessionElementsAggregateFunction;
import com.getindata.flink.sessionizer.function.SessionProcessor;
import com.getindata.flink.sessionizer.model.Event;
import com.getindata.flink.sessionizer.model.Key;
import com.getindata.flink.sessionizer.model.OrderWithSessions;
import com.getindata.flink.sessionizer.model.Session;
import com.getindata.flink.sessionizer.sessionwindow.SessionElementWindowAssigner;
import com.getindata.flink.sessionizer.sink.KafkaJsonSinkFactory;
import com.getindata.flink.sessionizer.source.EventKafkaSource;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {

    public static void main(String[] args) throws Exception {
        var config = new JobConfig(ConfigFactory.load());
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        build(config, env);
        env.execute();
    }

    public static void build(JobConfig config, StreamExecutionEnvironment env) throws Exception {
        DataStream<Event> events = EventKafkaSource.create(
                env, config.getBootStrapServers(), config.getInputTopic(), 1, OffsetsInitializer.earliest());

        KafkaSink<OrderWithSessions> orderWithSessionsSink = KafkaJsonSinkFactory.create(
                config.getBootStrapServers(),
                config.getOutputTopic(),
                (KeySelector<OrderWithSessions, String>) orderWithSessions -> orderWithSessions.getOrder().getId()
        );

        SingleOutputStreamOperator<Session> sessions = events
                .keyBy((KeySelector<Event, Key>) Event::getKey)
                .window(new SessionElementWindowAssigner(config.getSessionInactivityGap().toMillis()))
                .aggregate(new SessionElementsAggregateFunction(config.getSessionInactivityGap().toMillis()));

        SingleOutputStreamOperator<Session> enrichedSessions = sessions
                .keyBy(Session::getUserId)
                .process(new SessionProcessor());

        SideOutputDataStream<OrderWithSessions> orderWithSessions = enrichedSessions
                .getSideOutput(SessionProcessor.orderWithSessionsOutputTag);

        enrichedSessions.print(); // TODO
        orderWithSessions.sinkTo(orderWithSessionsSink);
    }
}
