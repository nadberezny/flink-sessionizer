FROM flink:1.20.1-java17

RUN mkdir -p /opt/flink/usrlib

ADD build/libs/flink-sessionizer-0.1-SNAPSHOT-all.jar /opt/flink/usrlib/flink-sessionizer.jar
