package com.getindata.flink.sessionizer.test.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

public class AttributionServiceContainer extends GenericContainer<AttributionServiceContainer> {

    public AttributionServiceContainer(Network network) {
        super("nadberezny/attribution-service:latest");

        this.withNetwork(network)
                .withNetworkAliases("attribution-service")
                .withCreateContainerCmdModifier(modifier -> modifier.withName("attribution-service"))
                .withExposedPorts(8080);
    }
}
