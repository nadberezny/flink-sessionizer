package com.getindata.clickhouse.evolution;

import com.getindata.clickhouse.evolution.context.DataMigrationContextFilter;
import com.getindata.clickhouse.evolution.context.TestMigrationContextFilter;
import com.typesafe.config.ConfigFactory;
import liquibase.Contexts;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class SchemaEvolutionApp {

    public static void main(String[] args) {
        LiquibaseConfig liquibaseConfig = getConfig();
        System.setProperty("kafka.url", liquibaseConfig.getKafkaUrl());
        System.setProperty("kafka.topic.sessions", liquibaseConfig.getSessionsTopic());
        System.setProperty("kafka.topic.ordersattributed", liquibaseConfig.getAttributedOrdersTopic());
        System.setProperty("schemaregistry.url", liquibaseConfig.getSchemaRegistryUrl());
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
            liquibaseRunner.update(getContexts(liquibaseRunner, liquibaseConfig));
            log.info("Migration finished");
        }
    }

    private static LiquibaseConfig getConfig() {
        var conf = ConfigFactory.load();
        var liquibaseConfig = new LiquibaseConfig(conf.getConfig("liquidbase"));
        log.info("Liquibase config: {}", liquibaseConfig);
        return liquibaseConfig;
    }

    private static Set<String> getContexts(LiquibaseRunner liquibaseRunner, LiquibaseConfig liquibaseConfig) {
        return liquibaseRunner.withLiquibase((liquibase) -> {
            try {
                return liquibase
                        .getDatabaseChangeLog()
                        .getChangeSets()
                        .stream()
                        .flatMap(cs -> cs.getContextFilter().getContexts().stream())
                        .filter(new DataMigrationContextFilter(liquibaseConfig))
                        .filter(new TestMigrationContextFilter(liquibaseConfig))
                        .collect(Collectors.toSet());
            } catch (LiquibaseException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
