FROM amazoncorretto:21-alpine-jdk

ADD ./build/install/clickhouse-evolution /app

WORKDIR /app

ENTRYPOINT ["./bin/clickhouse-evolution"]
