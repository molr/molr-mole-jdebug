/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.jvm.impl;

import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.jvm.AbstractJvmSpawner;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
<<<<<<< HEAD
=======
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
>>>>>>> gui
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link AgentRunnerSpawner} that uses an the {@link ProcessBuilder} class to start a new JVM
 * running cern.jarrace.agent.AgentRunner#main.
 *
 * @author tiagomr
 */
public class SimpleAgentRunnerSpawner extends AbstractJvmSpawner implements AgentRunnerSpawner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAgentRunnerSpawner.class);
    private static final String AGENT_RUNNER_MAIN_CASS = "cern.jarrace.agent.AgentRunner";

    @Override
    public String spawnAgentRunner(Service service, String jarPath, List<String> args) throws Exception {
        if(args == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        List<String> arguments = new ArrayList<>();
        arguments.add("-cp");
        arguments.add(jarPath);
        arguments.add(AGENT_RUNNER_MAIN_CASS);
        arguments.add(service.getAgentName());
        arguments.add(service.getClassName());
        if (args != null) {
            for (String argument : args) {
                if (!argument.isEmpty()) {
                    arguments.add(argument);
                }
            }
        }

        Process process = spawnJvm(arguments);

        StringBuilder stringBuilder = new StringBuilder();
<<<<<<< HEAD
        BufferedInputStream bs = new BufferedInputStream(process.getInputStream());

        while (process.isAlive()) {
            stringBuilder.append(bs.read());
=======
        InputStreamReader inputReader = new InputStreamReader(process.getInputStream());
        BufferedReader reader = new BufferedReader(inputReader);
        while(process.isAlive()) {
            final String line = reader.readLine();
            if (line == null) {
                break;
            }
            stringBuilder.append(line);
>>>>>>> gui
        }

        return stringBuilder.toString();
    }
}
