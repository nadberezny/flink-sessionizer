package com.getindata.flink.sessionizer.test.containers;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.awaitility.Awaitility.await;

public class CDCEvolutionContainer extends GenericContainer<CDCEvolutionContainer> {

    public CDCEvolutionContainer(Network network, String liquibaseSchema, String cdcDatabase, String cdcUser, String cdcPassword, MySQLContainer<?> mysql) {
        super("nadberezny/cdc-evolution:latest");

        this.withNetwork(network)
                .withEnv("JDBC_CONNECTION_URL", "jdbc:mysql://" + mysql.getNetworkAliases().get(0) + ":" + 3306)
                .withEnv("LIQUIBASE_USERNAME", mysql.getUsername())
                .withEnv("LIQUIBASE_PASSWORD", mysql.getPassword())
                .withEnv("LIQUIBASE_SCHEMA", liquibaseSchema)
                .withEnv("CDC_DATABASE", cdcDatabase)
                .withEnv("CDC_USER", cdcUser)
                .withEnv("CDC_PASSWORD", cdcPassword)
                .withCreateContainerCmdModifier(modifier -> modifier.withName("cdc-" + randomAlphabetic(4)))
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(CDCEvolutionContainer.class)));
    }

    @Override
    public void start() {
        super.start();
        await()
                .atMost(60, SECONDS)
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> this.getLogs().contains("Migration finished"));
    }
}
