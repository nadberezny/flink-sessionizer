package com.getindata.flink.sessionizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getindata.flink.sessionizer.model.OrderWithAttributedSessions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalAttributionServiceTest {

    @Test
    void testAttributionServiceReturnsExpectedResult() throws Exception {
        OrderWithAttributedSessions result = new ObjectMapper().readValue(readResourceFile("attribution-service/response1.json"), OrderWithAttributedSessions.class);
        assertThat(result).isNotNull();
    }

    private String readResourceFile(String resourcePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource file not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
