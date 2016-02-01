/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.jvm.impl;

import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link AgentRunnerSpawner} that uses an the {@link ProcessBuilder} class to start a new JVM
 * running cern.jarrace.agent.AgentRunner#main.
 *
 * @author tiagomr
 */
public class SimpleAgentRunnerSpawner implements AgentRunnerSpawner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAgentRegistrySpawner.class);
    private static final String AGENT_RUNNER_MAIN_CASS = "cern.jarrace.agent.AgentRunner";


    @Override
    public String spawnAgentRunner(Service service, String jarPath, List<String> args) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(String.format("%s/bin/java", System.getProperty("java.home")));
        command.add("-cp");
        command.add(jarPath);
        command.add(AGENT_RUNNER_MAIN_CASS);
        command.add(service.getAgentName());
        command.add(service.getClazz());
        if (args != null) {
            for (String argument : args) {
                if (!argument.isEmpty()) {
                    command.add(argument);
                }
            }
        }

        LOGGER.info("Starting agent runner [{}]", command.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        StringBuilder stringBuilder = new StringBuilder();
        BufferedInputStream bs = new BufferedInputStream(process.getInputStream());

        while (process.isAlive()) {
            stringBuilder.append(bs.read());
        }

        return stringBuilder.toString();
    }
}
