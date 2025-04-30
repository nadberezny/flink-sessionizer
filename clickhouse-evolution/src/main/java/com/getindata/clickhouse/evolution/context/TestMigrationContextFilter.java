package com.getindata.clickhouse.evolution.context;

import com.getindata.clickhouse.evolution.LiquibaseConfig;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

import static com.getindata.clickhouse.evolution.LiquibaseConfig.TEST_MIGRATION_CONTEXT;

@RequiredArgsConstructor
public class TestMigrationContextFilter implements Predicate<String> {
    private final LiquibaseConfig config;

    @Override
    public boolean test(String ctx) {
        return config.isSkipTestMigrations() && Objects.equals(ctx, TEST_MIGRATION_CONTEXT)
                ? false
                : config.getContexts().isEmpty() || config.getContexts().contains(ctx);
    }

}
