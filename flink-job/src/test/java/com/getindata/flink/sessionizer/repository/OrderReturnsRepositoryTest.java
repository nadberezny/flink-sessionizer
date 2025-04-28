package com.getindata.flink.sessionizer.repository;

import com.getindata.flink.sessionizer.model.cdc.OrderReturn;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static com.getindata.flink.sessionizer.repository.OrderReturnsRepository.utcCalendar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderReturnsRepositoryTest {

    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void startContainer() {
        mysql.start();
    }

    @AfterAll
    static void stopContainer() {
        if (mysql.isRunning()) {
            mysql.stop();
        }
    }

    private OrderReturnsRepository repository;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create the repository with MySQL container connection details
        String jdbcUrl = mysql.getJdbcUrl();
        String username = mysql.getUsername();
        String password = mysql.getPassword();

        repository = new OrderReturnsRepository(jdbcUrl, "testdb", "order_returns", username, password);

        // Create a connection for test verification
        connection = DriverManager.getConnection(jdbcUrl, username, password);

        // Create the order_returns table
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS order_returns (" +
                "order_id VARCHAR(255) PRIMARY KEY, " +
                "return_timestamp TIMESTAMP(6))")) {
            statement.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Drop the table after each test
        try (PreparedStatement statement = connection.prepareStatement("DROP TABLE IF EXISTS order_returns")) {
            statement.executeUpdate();
        }

        // Close the connection
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testInsert() throws SQLException {
        // Given
        Instant now = Instant.now();
        OrderReturn orderReturn = new OrderReturn("test-id", now);

        // When
        boolean result = repository.insert(orderReturn);

        // Then
        assertTrue(result);

        // Verify the record was inserted
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM order_returns WHERE order_id = ?")) {
            statement.setString(1, "test-id");
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next());
            assertEquals("test-id", resultSet.getString("order_id"));
            assertEquals(now.toEpochMilli(), resultSet.getTimestamp("return_timestamp", utcCalendar).toInstant().toEpochMilli());
            assertFalse(resultSet.next());
        }
    }

    @Test
    void testUpdate() throws SQLException {
        // Given
        Instant now = Instant.now();
        OrderReturn orderReturn = new OrderReturn("test-id", now);
        repository.insert(orderReturn);

        // When
        Instant updated = Instant.now();
        orderReturn.setReturnTimestamp(updated);
        boolean result = repository.update(orderReturn);

        // Then
        assertTrue(result);

        // Verify the record was updated
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM order_returns WHERE order_id = ?")) {
            statement.setString(1, "test-id");
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next());
            assertEquals("test-id", resultSet.getString("order_id"));
            assertEquals(Timestamp.from(updated), resultSet.getTimestamp("return_timestamp", utcCalendar));
            assertFalse(resultSet.next());
        }
    }

    @Test
    void testDelete() throws SQLException {
        // Given
        Instant now = Instant.now();
        OrderReturn orderReturn = new OrderReturn("test-id", now);
        repository.insert(orderReturn);

        // When
        boolean result = repository.delete("test-id");

        // Then
        assertTrue(result);

        // Verify the record was deleted
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM order_returns WHERE order_id = ?")) {
            statement.setString(1, "test-id");
            ResultSet resultSet = statement.executeQuery();

            assertFalse(resultSet.next());
        }
    }
}
