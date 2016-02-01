/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.jvm.impl;

import cern.jarrace.controller.jvm.AgentRegistrySpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link AgentRegistrySpawner} that uses an the {@link ProcessBuilder} class to start a new JVM
 * running cern.jarrace.agent.ContainerRegistry#main.
 *
 * @author tiagomr
 */
public class SimpleAgentRegistrySpawner implements AgentRegistrySpawner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAgentRegistrySpawner.class);
    private static final String AGENT_CONTAINER_MAIN_CASS = "cern.jarrace.agent.ContainerRegistry";

    @Value("${server.port}")
    private int controllerPort;

    @Value("${server.interface}")
    private String controllerInterface;

    @Override
    public void spawnAgentRegistry(String containerName, String jarPath) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(String.format("%s/bin/java", System.getProperty("java.home")));
        command.add("-cp");
        command.add(jarPath);
        command.add(AGENT_CONTAINER_MAIN_CASS);
        command.add(containerName);
        command.add(jarPath);
        command.add(String.format("%s:%s", controllerInterface, controllerPort));

        LOGGER.info("Starting agent container [{}]", command.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO().start();
    }
}
