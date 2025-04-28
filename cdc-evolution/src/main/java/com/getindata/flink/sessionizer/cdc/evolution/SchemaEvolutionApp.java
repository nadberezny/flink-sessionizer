package com.getindata.flink.sessionizer.cdc.evolution;

import com.getindata.flink.sessionizer.cdc.evolution.config.CDCConfig;
import com.getindata.flink.sessionizer.cdc.evolution.config.LiquibaseConfig;
import com.typesafe.config.ConfigFactory;
import liquibase.Contexts;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class SchemaEvolutionApp {

    public static void main(String[] args) {
        LiquibaseConfig liquibaseConfig = getConfig();
        CDCConfig cdcConfig = liquibaseConfig.getCdcConfig();

        log.info("Config: {}", liquibaseConfig);

        System.setProperty("cdc.user", cdcConfig.getUser());
        System.setProperty("cdc.password", cdcConfig.getPassword());
        System.setProperty("cdc.database", cdcConfig.getDatabase());

        var liquibaseRunner = new LiquibaseRunner(liquibaseConfig, new ClassLoaderResourceAccessor());

        if (liquibaseConfig.isSyncChangeLog()) {
            log.info("Syncing liquibase change log");
            liquibaseRunner.withLiquibase((liquibase) -> {
                try {
                    liquibase.changeLogSync(new Contexts());
                } catch (LiquibaseException e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("ChangeLog Sync finished");
        } else {
            log.info("Running liquibase migration");
            liquibaseRunner.update(List.of());
            log.info("Migration finished");
        }
    }

    private static LiquibaseConfig getConfig() {
        var conf = ConfigFactory.load();
        var liquibaseConfig = new LiquibaseConfig(conf.getConfig("liquibase"));
        log.info("Liquibase config: {}", liquibaseConfig);
        return liquibaseConfig;
    }
}
