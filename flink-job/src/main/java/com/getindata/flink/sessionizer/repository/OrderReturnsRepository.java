package com.getindata.flink.sessionizer.repository;

import com.getindata.flink.sessionizer.model.cdc.OrderReturn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import static com.getindata.flink.sessionizer.commons.Commons.TIME_ZONE;

/**
 * Repository for managing OrderReturn entities in the database.
 * Provides methods for insert, update, and delete operations.
 */
@Slf4j
@RequiredArgsConstructor
public class OrderReturnsRepository {

    public static final Calendar utcCalendar = Calendar.getInstance(TIME_ZONE);

    private final String jdbcUrl;
    private final String database;
    private final String table;
    private final String username;
    private final String password;

    /**
     * Inserts a new OrderReturn into the database.
     *
     * @param orderReturn the OrderReturn to insert
     * @return true if the insert was successful, false otherwise
     */
    public boolean insert(OrderReturn orderReturn) {
        String sql = "INSERT INTO " + database + "." + table + " (order_id, return_timestamp) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, orderReturn.getOrderId());
            if (orderReturn.getReturnTimestamp() != null) {
                statement.setTimestamp(2, java.sql.Timestamp.from(orderReturn.getReturnTimestamp()), utcCalendar);
            } else {
                statement.setNull(2, java.sql.Types.TIMESTAMP);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error inserting OrderReturn: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Updates an existing OrderReturn in the database.
     *
     * @param orderReturn the OrderReturn to update
     * @return true if the update was successful, false otherwise
     */
    public boolean update(OrderReturn orderReturn) {
        String sql = "UPDATE " + database + "." + table +" SET return_timestamp = ? WHERE order_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (orderReturn.getReturnTimestamp() != null) {
                statement.setTimestamp(1, java.sql.Timestamp.from(orderReturn.getReturnTimestamp()), utcCalendar);
            } else {
                statement.setNull(1, java.sql.Types.TIMESTAMP);
            }
            statement.setString(2, orderReturn.getOrderId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error updating OrderReturn: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Deletes an OrderReturn from the database.
     *
     * @param id the ID of the OrderReturn to delete
     * @return true if the delete was successful, false otherwise
     */
    public boolean delete(String id) {
        String sql = "DELETE FROM " + database + "." + table + " WHERE order_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error deleting OrderReturn: {}", e.getMessage(), e);
            return false;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
