package com.getindata.flink.sessionizer.function.cdc;

import com.getindata.flink.sessionizer.model.cdc.OrderReturn;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapToOrderReturnTest {

    @Test
    public void testMapCdcJsonToOrderReturn() throws Exception {
        // given
        String cdcJson = readResourceFile("cdc-json/order-return1.json");
        MapToOrderReturn mapToOrderReturn = new MapToOrderReturn();
        
        // when
        OrderReturn result = mapToOrderReturn.map(cdcJson);
        
        // then
        assertEquals("id1", result.getOrderId());
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
