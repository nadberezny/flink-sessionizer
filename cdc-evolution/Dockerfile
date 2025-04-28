FROM amazoncorretto:11-alpine-jdk

ADD ./build/install/cdc-evolution /app

WORKDIR /app

ENTRYPOINT ["./bin/cdc-evolution"]
