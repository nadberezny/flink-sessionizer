FROM amazoncorretto:21-alpine-jdk

ADD ./build/install/clickstream-generator /app

WORKDIR /app

ENTRYPOINT ["./bin/clickstream-generator"]

