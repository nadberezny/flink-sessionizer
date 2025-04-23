package com.getindata.flink.sessionizer.config;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.Duration;

@Getter
@RequiredArgsConstructor
public class CDCConfig implements Serializable {

    private final boolean enabled;
    private final Duration stateTTL;
    private final String hostname;
    private final int port;
    private final String database;
    private final String table;
    private final String username;
    private final String password;

    public CDCConfig(Config jobConfig) {
        var cdcEnabled = jobConfig.getBoolean("cdc.enabled");

        if (cdcEnabled) {
            var cdcConfig = jobConfig.getConfig("cdc");
            stateTTL = Duration.parse(cdcConfig.getString("stateTTL"));
            hostname = cdcConfig.getString("hostname");
            port = cdcConfig.getInt("port");
            database = cdcConfig.getString("database");
            table = cdcConfig.getString("table");
            username = cdcConfig.getString("username");
            password = cdcConfig.getString("password");
        } else {
            stateTTL = Duration.ZERO;
            hostname = null;
            port = 0;
            database = null;
            table = null;
            username = null;
            password = null;
        }
        enabled = cdcEnabled;
    }
}
