package com.getindata.flink.sessionizer.cdc.evolution.config;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class CDCConfig {

    private final String user;
    @ToString.Exclude
    private final String password;
    private final String database;

    public CDCConfig(Config config) {
        this.user = config.getString("user");
        this.password = config.getString("password");
        this.database = config.getString("database");
    }
}
