FROM amazoncorretto:21-alpine-jdk

ADD ./build/install/datagen /app

WORKDIR /app

ENTRYPOINT ["./bin/datagen"]

