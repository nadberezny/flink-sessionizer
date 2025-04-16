package com.getindata.flink.sessionizer.config;

import com.typesafe.config.Config;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.Properties;

@Value
@RequiredArgsConstructor
public class KafkaConfig implements Serializable {

    String bootstrapServers;
    String securityProtocol;
    SaslConfig sasl;
    String clickStreamTopic;
    String sessionsTopic;
    String attributedOrdersTopic;

    public KafkaConfig(Config config) {
        var kafkaConfig = config.getConfig("kafka");
        bootstrapServers = kafkaConfig.getString("bootstrapServers");

        securityProtocol = kafkaConfig.getString("securityProtocol");
        if (securityProtocol.equals("SASL_SSL")) {
            var saslConfig = kafkaConfig.getConfig("sasl");
            sasl = new SaslConfig(saslConfig.getString("jaas.config"), saslConfig.getString("mechanism"));
        } else {
            sasl = null;
        }

        clickStreamTopic = kafkaConfig.getString("clickStreamTopic");
        sessionsTopic = kafkaConfig.getString("sessionsTopic");
        attributedOrdersTopic = kafkaConfig.getString("attributedOrdersTopic");
    }

    public Properties getKafkaProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        if (securityProtocol.equals("SASL_SSL")) {
            props.put("security.protocol", "SASL_SSL");
            props.put("sasl.mechanism", sasl.getMechanism());
            props.put("sasl.jaas.config", sasl.JaasConfig);
        }
        return props;
    }

    @Value
    public static class SaslConfig implements Serializable {
        String JaasConfig;
        String mechanism;
    }
}
