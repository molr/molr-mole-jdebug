/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.jvm.impl;

import cern.molr.controller.jvm.AbstractJvmSpawner;
import cern.molr.controller.jvm.MoleRegistrySpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link MoleRegistrySpawner} that uses an the {@link ProcessBuilder} class to start a new JVM
 * running cern.jarrace.agent.ContainerRegistry#main.
 *
 * @author tiagomr
 */
public class SimpleMoleRegistrySpawner extends AbstractJvmSpawner implements MoleRegistrySpawner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMoleRegistrySpawner.class);
    private static final String AGENT_CONTAINER_MAIN_CASS = "cern.jarrace.agent.ContainerRegistry";

    @Value("${server.port}")
    private int controllerPort;

    @Value("${server.interface}")
    private String controllerInterface;

    @Override
    public void spawnAgentRegistry(String containerName, String jarPath) throws Exception {
        List<String> arguments = new ArrayList<>();
        arguments.add("-cp");
        arguments.add(jarPath);
        arguments.add(AGENT_CONTAINER_MAIN_CASS);
        arguments.add(containerName);
        arguments.add(jarPath);
        arguments.add(String.format("%s:%s", controllerInterface, controllerPort));
        spawnJvm(arguments);
    }
}
