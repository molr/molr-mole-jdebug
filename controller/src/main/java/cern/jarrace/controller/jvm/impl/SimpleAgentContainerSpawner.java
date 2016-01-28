package cern.jarrace.controller.jvm.impl;

import cern.jarrace.controller.jvm.AgentContainerSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timartin on 27/1/2016.
 */
public class SimpleAgentContainerSpawner implements AgentContainerSpawner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAgentContainerSpawner.class);
    private static final String AGENT_CONTAINER_MAIN_CASS = "cern.jarrace.agent.AgentContainer";

    @Value("${server.port}")
    private int controllerPort;

    @Value("${server.interface}")
    private String controllerInterface;

    @Override
    public void spawnJvm(String containerName, String jarPath) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(String.format("%s/bin/java", System.getProperty("java.home")));
        command.add("-cp");
        command.add(AGENT_CONTAINER_MAIN_CASS);
        command.add(String.format("%s:%s", controllerInterface, controllerPort));

        LOGGER.info(String.format("Starting agent container [%s]", command.toString()));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO().start();
    }
}