package com.getindata.clickhouse.evolution;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class LiquibaseConfig {

    public final static String DATA_MIGRATION_CONTEXT = "data_migration";

    public final static String TEST_MIGRATION_CONTEXT = "test";

    private final String jdbcConnectionUrl;

    private final String liquibaseSchema;

    private final String changeLogPath;

    private final boolean syncChangeLog;

    private final boolean skipDataMigrations;

    private final boolean skipTestMigrations;

    private final List<String> contexts;

    private final String kafkaUrl;

    private final String sessionsTopic;

    private final String attributedOrdersTopic;

    private final String schemaRegistryUrl;

    public LiquibaseConfig(Config config) {
        var clickhouseConf = config.getConfig("clickhouse");
        this.jdbcConnectionUrl = clickhouseConf.getString("jdbcConnectionUrl");
        this.liquibaseSchema = clickhouseConf.getString("liquibaseSchema");
        this.changeLogPath = clickhouseConf.getString("changeLogPath");
        this.syncChangeLog = clickhouseConf.getBoolean("syncChangeLog");
        this.skipDataMigrations = clickhouseConf.getBoolean("skipDataMigrations");
        this.skipTestMigrations = clickhouseConf.getBoolean("skipTestMigrations");
        var contextsStr = clickhouseConf.getString("contexts").trim();
        this.contexts = contextsStr.isEmpty()
                ? List.of()
                : List.of(clickhouseConf.getString("contexts").split(","));

        var kafkaConf = config.getConfig("kafka");
        this.kafkaUrl = kafkaConf.getString("url");
        this.sessionsTopic = kafkaConf.getString("sessionsTopic");
        this.attributedOrdersTopic = kafkaConf.getString("attributedOrdersTopic");

        this.schemaRegistryUrl = config.getString("schemaregistry.url");
    }
}
