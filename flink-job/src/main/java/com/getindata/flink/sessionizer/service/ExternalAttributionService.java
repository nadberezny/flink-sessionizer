package com.getindata.flink.sessionizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import com.getindata.flink.sessionizer.model.OrderWithSessions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ExternalAttributionService implements AttributionService {

    private final HttpClient httpClient;
    private final String serviceUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExternalAttributionService(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public OrderWithAttributedSessions apply(OrderWithSessions orderWithSessions) {
        try {
            String requestBody = objectMapper.writeValueAsString(orderWithSessions);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serviceUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Attribution service returned status code: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), OrderWithAttributedSessions.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error calling attribution service", e);
        }
    }

    @Override
    public void close() throws Exception {
    }
}
