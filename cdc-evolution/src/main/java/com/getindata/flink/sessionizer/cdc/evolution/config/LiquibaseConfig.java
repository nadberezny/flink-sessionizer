package com.getindata.flink.sessionizer.cdc.evolution.config;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class LiquibaseConfig {

    private final String jdbcConnectionUrl;

    private final String username;

    @ToString.Exclude
    private final String password;

    private final String liquibaseSchema;

    private final String changeLogPath;

    private final boolean isSyncChangeLog = false;

    private final CDCConfig cdcConfig;

    public LiquibaseConfig(Config config) {
        this.jdbcConnectionUrl = config.getString("jdbcConnectionUrl");
        this.username = config.getString("username");
        this.password = config.getString("password");
        this.liquibaseSchema = config.getString("liquibaseSchema");
        this.changeLogPath = config.getString("changeLogPath");
        this.cdcConfig = new CDCConfig(config.getConfig("cdc"));
    }
}
