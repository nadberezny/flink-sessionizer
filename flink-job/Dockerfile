FROM flink:2.1.0-scala_2.12-java21

# Add s3 plugin for s3 state backend
RUN mkdir /opt/flink/plugins/s3-fs-presto
RUN cp /opt/flink/opt/flink-s3-fs-presto-2.1.0.jar /opt/flink/plugins/s3-fs-presto/

# Add s3 plugin for s3 sink
RUN mkdir /opt/flink/plugins/s3-fs-hadoop
RUN cp /opt/flink/opt/flink-s3-fs-hadoop-2.1.0.jar /opt/flink/plugins/s3-fs-hadoop/

# job jar
RUN mkdir -p /opt/flink/usrlib
ADD ./build/libs/flink-job-0.1-SNAPSHOT-all.jar /opt/flink/usrlib/flink-sessionizer.jar
