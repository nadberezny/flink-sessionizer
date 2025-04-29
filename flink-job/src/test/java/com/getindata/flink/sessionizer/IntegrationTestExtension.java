package com.getindata.flink.sessionizer;

import com.getindata.flink.sessionizer.config.CDCConfig;
import com.getindata.flink.sessionizer.config.JobConfig;
import com.getindata.flink.sessionizer.config.KafkaConfig;
import com.getindata.flink.sessionizer.repository.OrderReturnsRepository;
import com.getindata.flink.sessionizer.serde.input.ClickStreamEventJson;
import com.getindata.flink.sessionizer.serde.kafka.JsonSerializer;
import com.getindata.flink.sessionizer.serde.kafka.OrderWithAttributedSessionsDeserializer;
import com.getindata.flink.sessionizer.serde.kafka.SessionsDeserializer;
import com.getindata.flink.sessionizer.serde.output.AttributedOrderJson;
import com.getindata.flink.sessionizer.serde.output.SessionJson;
import com.getindata.flink.sessionizer.service.ExternalAttributionServiceSupplier;
import com.getindata.flink.sessionizer.test.containers.AttributionServiceContainer;
import com.getindata.flink.sessionizer.test.containers.CDCEvolutionContainer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.junit5.MiniClusterExtension;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.redpanda.RedpandaContainer;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Slf4j
public class IntegrationTestExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final String clickStreamTopic = "click-stream";
    public static final String sessionsTopic = "sessions";
    public static final String attributedOrdersTopic = "attributed-orders";
    public static final String cdcDatabase = "cdcdb";

    @RegisterExtension
    public static final MiniClusterExtension flinkCluster = new MiniClusterExtension(
            new MiniClusterResourceConfiguration.Builder()
                    .setNumberTaskManagers(1)
                    .setConfiguration(new Configuration())
                    .build());

    private static final Network network = Network.newNetwork();

    private final AttributionServiceContainer attributionService = new AttributionServiceContainer(network);

    private final RedpandaContainer redpanda = new RedpandaContainer("docker.redpanda.com/redpandadata/redpanda:v23.1.2")
            .withNetwork(network)
            .withCreateContainerCmdModifier(modifier -> modifier.withName("kafka-" + randomAlphabetic(4)))
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(RedpandaContainer.class)));

    private final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withNetwork(network)
            .withCreateContainerCmdModifier(modifier -> modifier.withName("mysql-" + randomAlphabetic(4)))
            .withDatabaseName("default")
            .withUsername("root")
            .withPassword("test")
            // Enable binary logging for CDC
            .withCommand("--server-id=1",
                    "--log-bin=mysql-bin",
                    "--binlog_format=ROW",
                    "--binlog_row_metadata=FULL")
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(MySQLContainer.class)));

    private CDCEvolutionContainer cdcEvolution;

    private AdminClient kafkaAdmin;

    private KafkaProducer<String, ClickStreamEventJson> clickStreamProducer;

    private KafkaConsumer<String, SessionJson> sessionsConsumer;

    private KafkaConsumer<String, AttributedOrderJson> orderWithSessionsConsumer;

    private OrderReturnsRepository orderReturnsRepository;

    private StreamExecutionEnvironment env;

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (kafkaAdmin != null) {
            kafkaAdmin.close();
        }
        if (mysql.isRunning()) {
            mysql.stop();
        }
        if (cdcEvolution.isRunning()) {
            cdcEvolution.stop();
        }
        if (redpanda.isRunning()) {
            redpanda.stop();
        }
        attributionService.stop();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (env != null) {
            env.close();
        }
        if (clickStreamProducer != null) {
            clickStreamProducer.close();
        }
        if (sessionsConsumer != null) {
            sessionsConsumer.close();
        }
        if (orderWithSessionsConsumer != null) {
            orderWithSessionsConsumer.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        attributionService.start();
        redpanda.start();
        mysql.start();
        cdcEvolution = new CDCEvolutionContainer(network, "default", cdcDatabase, "cdcuser", "cdcpassword", mysql);
        cdcEvolution.start();


        kafkaAdmin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, redpanda.getBootstrapServers()));
        kafkaAdmin.createTopics(List.of(
                new NewTopic(clickStreamTopic, Optional.empty(), Optional.empty()),
                new NewTopic(sessionsTopic, Optional.empty(), Optional.empty()),
                new NewTopic(attributedOrdersTopic, Optional.empty(), Optional.empty())
        ));
        orderReturnsRepository = new OrderReturnsRepository(mysql.getJdbcUrl(), cdcDatabase, "order_returns", mysql.getUsername(), mysql.getPassword());
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        clickStreamProducer = new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, redpanda.getBootstrapServers(),
                ProducerConfig.CLIENT_ID_CONFIG, randomUUID().toString(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName()
        ));

        sessionsConsumer = new KafkaConsumer<>(Map.of(
                BOOTSTRAP_SERVERS_CONFIG, redpanda.getBootstrapServers(),
                GROUP_ID_CONFIG, randomUUID().toString(),
                AUTO_OFFSET_RESET_CONFIG, "earliest",
                KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                VALUE_DESERIALIZER_CLASS_CONFIG, SessionsDeserializer.class.getName()
        ));
        sessionsConsumer.subscribe(List.of(sessionsTopic));

        orderWithSessionsConsumer = new KafkaConsumer<>(Map.of(
                BOOTSTRAP_SERVERS_CONFIG, redpanda.getBootstrapServers(),
                GROUP_ID_CONFIG, randomUUID().toString(),
                AUTO_OFFSET_RESET_CONFIG, "earliest",
                KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                VALUE_DESERIALIZER_CLASS_CONFIG, OrderWithAttributedSessionsDeserializer.class.getName()
        ));
        orderWithSessionsConsumer.subscribe(List.of(attributedOrdersTopic));

        var kafkaConfig = new KafkaConfig(redpanda.getBootstrapServers(), "PLAIN", null, clickStreamTopic, sessionsTopic, attributedOrdersTopic);
        var cdcConfig = new CDCConfig(
                true,
                Duration.ofDays(30),
                mysql.getHost(),
                mysql.getMappedPort(3306),
                cdcDatabase,
                cdcDatabase + ".order_returns",
                mysql.getUsername(),
                mysql.getPassword()
        );
        var jobConfig = new JobConfig(Duration.ofMinutes(30), null, kafkaConfig, cdcConfig);
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        setupCheckpointing(env);
        Main.buildTopology(jobConfig, env, new ExternalAttributionServiceSupplier("http://localhost:" + attributionService.getMappedPort(8080)));
        env.executeAsync();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(IntegrationTextCtx.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new IntegrationTextCtx(clickStreamProducer, sessionsConsumer, orderWithSessionsConsumer, orderReturnsRepository);
    }

    @SneakyThrows
    private void setupCheckpointing(StreamExecutionEnvironment env) {
        File checkpointDir = Files.createTempDirectory(getClass().getSimpleName() + "-checkpoint-" + RandomStringUtils.randomAlphanumeric(6)).toFile();
        checkpointDir.deleteOnExit();
        env.enableCheckpointing(500);
        env.getCheckpointConfig().setCheckpointStorage("file://" + checkpointDir.getAbsolutePath());
    }

    @lombok.Value
    public static class IntegrationTextCtx {
        KafkaProducer<String, ClickStreamEventJson> clickStreamProducer;
        KafkaConsumer<String, SessionJson> sessionsConsumer;
        KafkaConsumer<String, AttributedOrderJson> orderWithSessionsConsumer;
        OrderReturnsRepository orderReturnsRepository;
    }
}

