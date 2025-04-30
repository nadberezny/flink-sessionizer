package com.getindata.sessionizer.datagen.producer;

import com.getindata.sessionizer.datagen.serde.kafka.Event;
import com.getindata.sessionizer.datagen.serde.kafka.EventKey;
import com.getindata.sessionizer.datagen.serde.t2.MetaInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.joining;

@Slf4j
public class TrackeriTwoProducer implements Producer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final URI baseUrl;

    private final HttpClient httpClient;

    public TrackeriTwoProducer(URI baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient
                .newBuilder()
                .executor(Executors.newCachedThreadPool())
                .build();
    }

    @Override
    @SneakyThrows
    public void send(EventKey eventKey, Event event) {
        MetaInfo.MetaInfoBuilder metaInfoBuilder = MetaInfo.builder()
                .withKey(eventKey.visitorId())
                .withCreatedAt(event.createdAt())
                .withSource(event.source())
                .withTrackedBy(event.trackedBy())
                .withFrontendId(event.frontendId())
                .withType(event.type());
        if (event.pageview() != null) {
            metaInfoBuilder.withPageview(event.pageview());
        } else {
            metaInfoBuilder.withOrder(event.order());
        }

        var metaInfoJson = objectMapper.writeValueAsString(metaInfoBuilder.build());
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("i", event.frontendId());
        requestParams.put("uid", eventKey.visitorId());
        requestParams.put("u", "https://www.dupa.ai");
        requestParams.put("e", event.type());
        requestParams.put("m", metaInfoJson);
        requestParams.put("v", "1.0.0");

        var encodedUrl = requestParams
                .keySet()
                .stream()
                .map(key -> key + "=" + encode(requestParams.get(key)))
                .collect(joining("&", baseUrl.toString() + "/api/track?", ""));

        var request = HttpRequest
                .newBuilder()
                .uri(URI.create(encodedUrl))
                .GET()
                .header("Cookie", "session=" + eventKey.visitorId())
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                .thenAccept(response -> log.info("Event sent: " + response.statusCode()))
                .exceptionally(e -> {
                    var msg = "Failed to send event: " + e.getMessage();
                    log.info(msg, e);
                    return null;
                });
    }

    @SneakyThrows
    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
