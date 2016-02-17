/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.jvm.impl;

import cern.molr.commons.domain.Service;
import cern.molr.jvm.JvmSpawnHelper;
import cern.molr.jvm.MoleSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Implementation of {@link MoleSpawner} that uses an the {@link ProcessBuilder} class to start a new JVM
 * running cern.molr.mole.GenericMoleRunner#main.
 * Executes the Moles as they are spawned.
 *
 * @author tiagomr
 */
public class SimpleMoleRunnerSpawner implements MoleSpawner<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMoleRunnerSpawner.class);
    private static final String AGENT_RUNNER_MAIN_CLASS = "cern.molr.mole.GenericMoleRunner";
    private static final String INSPECTOR_MAIN_CLASS = "cern.molr.inspector.CliMain";

    @Override
    public String spawnMoleRunner(Service service, String... args) throws Exception {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String spawnMoleRunner(Service service, String classpath, String... args) throws Exception {
        if (args == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        ProcessBuilder processBuilder = JvmSpawnHelper.getProcessBuilder(classpath, AGENT_RUNNER_MAIN_CLASS, args);
        return readFromProcess(processBuilder.start());
    }

    private String readFromProcess(Process process) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        BufferedReader bf = new BufferedReader(ir);
        while (process.isAlive()) {
            String lineRead = bf.readLine();
            if (lineRead != null) {
                stringBuilder.append(lineRead);
            }
        }
        return stringBuilder.toString();
    }
}
