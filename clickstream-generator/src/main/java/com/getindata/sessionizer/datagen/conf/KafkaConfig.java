package com.getindata.sessionizer.datagen.conf;

import com.typesafe.config.Config;
import lombok.Value;

import java.util.Properties;

@Value
public class KafkaConfig {

    String bootStrapServers;

    String outputTopic;

    String securityProtocol;

    SaslConfig sasl;

    public KafkaConfig(Config config) {
        var kafkaConfig = config.getConfig("kafka");
        bootStrapServers = kafkaConfig.getString("bootstrapServers");
        outputTopic = kafkaConfig.getString("outputTopic");
        securityProtocol = kafkaConfig.getString("securityProtocol");
        if (securityProtocol.equals("SASL_SSL")) {
            var saslConfig = kafkaConfig.getConfig("sasl");
            sasl = new SaslConfig(saslConfig.getString("jaas.config"), saslConfig.getString("mechanism"));
        } else {
            sasl = null;
        }
    }

    public Properties getKafkaProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootStrapServers);
        if (securityProtocol.equals("SASL_SSL")) {
            props.put("security.protocol", "SASL_SSL");
            props.put("sasl.mechanism", sasl.getMechanism());
            props.put("sasl.jaas.config", sasl.JaasConfig);
        }
        return props;
    }

    @Value
    public static class SaslConfig {
        String JaasConfig;
        String mechanism;
    }
}
