/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.jvm.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.jvm.JvmSpawnHelper;
import cern.molr.jvm.MoleSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation of {@link MoleSpawner} that uses an the {@link ProcessBuilder} class to start a new JVM
 * running cern.molr.mole.GenericMoleRunner#main.
 * Executes the Moles as they are spawned.
 *
 * @author tiagomr
 */
public class SimpleMoleRunnerSpawner implements MoleSpawner<Void> {

    //TODO test this class

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMoleRunnerSpawner.class);
    private static final String AGENT_RUNNER_MAIN_CLASS = "cern.molr.GenericMoleRunner";
    private static final String CURRENT_CLASSPATH_VALUE = System.getProperty("java.class.path");

    @Override
    public Void spawnMoleRunner(Mission mission, String... args) throws IOException {
        return spawnMoleRunner(mission, CURRENT_CLASSPATH_VALUE, args);
    }

    @Override
    public Void spawnMoleRunner(Mission mission, String classpath, String... args) throws IOException {
        if(mission == null) {
            throw new IllegalArgumentException("The mission must not be null");
        }

        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
        argsList.add(mission.getMoleClassName());
        argsList.add(mission.getMissionContentClassName());

        ProcessBuilder processBuilder = JvmSpawnHelper.getProcessBuilder(
                JvmSpawnHelper.appendToolsJarToClasspath(classpath),
                AGENT_RUNNER_MAIN_CLASS,
                argsList.toArray(new String[argsList.size()]));

        processBuilder.inheritIO().start();
        return null;
    }
}
