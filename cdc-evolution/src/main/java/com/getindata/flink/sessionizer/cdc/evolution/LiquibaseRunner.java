package com.getindata.flink.sessionizer.cdc.evolution;

import com.getindata.flink.sessionizer.cdc.evolution.config.LiquibaseConfig;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class LiquibaseRunner {

    private final LiquibaseConfig liquibaseConfig;

    private final ResourceAccessor resourceAccessor;

    @SneakyThrows
    public void update(Collection<String> contexts) {
        Connection connection = getConnection();
        Database database = getDatabase(connection);
        Liquibase liquibase = getLiquibase(database);
        liquibase.update(new Contexts(), new LabelExpression());
        // TODO: close resources
    }

    public void withLiquibase(Consumer<Liquibase> f) {
        withLiquibase(liquibase -> {
            f.accept(liquibase);
            return null;
        });
    }

    public <T> T withLiquibase(Function<Liquibase, T> f) {
        try (var connection = getConnection()) {
            try (var database = getDatabase(connection)) {
                try (var liquibase = getLiquibase(database)) {
                    return f.apply(liquibase);
                } catch (LiquibaseException e) {
                    log.error("Liquibase migration error", e);
                    throw new RuntimeException(e);
                }
            } catch (DatabaseException e) {
                log.error("Database error", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            log.error("Connection error", e);
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                liquibaseConfig.getJdbcConnectionUrl(), liquibaseConfig.getUsername(), liquibaseConfig.getPassword()
        );
    }

    private Database getDatabase(Connection connection) throws SQLException, DatabaseException {
        var database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        database.setDatabaseChangeLogTableName("liquibase_log");
        database.setDefaultSchemaName(liquibaseConfig.getLiquibaseSchema());

        return database;
    }

    private Liquibase getLiquibase(Database database) throws DatabaseException, SQLException {
        // If the changelog is not on the classpath, use a liquibase.resource.FileSystemResourceAccessor or other appropriate accessor
        return new Liquibase(liquibaseConfig.getChangeLogPath(), resourceAccessor, database);
    }
}
