package cern.jarrace.controller.jvm.impl;

import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by timartin on 28/01/2016.
 */
public class SimpleAgentRunnerSpawner implements AgentRunnerSpawner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAgentRegistrySpawner.class);
    private static final String AGENT_RUNNER_MAIN_CASS = "cern.jarrace.agent.AgentRunner";


    @Override
    public String spawnAgentRunner(Service service, String jarPath, String... args) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(String.format("%s/bin/java", System.getProperty("java.home")));
        command.add("-cp");
        command.add(jarPath);
        command.add(AGENT_RUNNER_MAIN_CASS);
        command.add(service.getAgentName());
        command.add(service.getClazz());
        if(args != null) {
            for (String argument : args) {
                if(!argument.isEmpty()) {
                    command.add(argument);
                }
            }
        }

        LOGGER.info("Starting agent runner [{}]", command.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputReader = new InputStreamReader(process.getInputStream());
        BufferedReader reader = new BufferedReader(inputReader);
        while(process.isAlive()) {
            final String line = reader.readLine();
            if (line == null) {
                break;
            }
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }
}
