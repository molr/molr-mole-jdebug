/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.jvm.impl;

import cern.molr.commons.domain.JdiMission;
import cern.molr.commons.mole.GenericMoleRunner;
import cern.molr.jvm.JvmSpawnHelper;
import cern.molr.jvm.MoleSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation of {@link MoleSpawner} that uses an the {@link ProcessBuilder} class to start a new JVM
 * running {@link cern.molr.commons.mole.GenericMoleRunner#main}.
 * Executes the Moles as they are spawned.
 *
 * @author tiagomr
 */
public class SimpleMoleRunnerSpawner implements MoleSpawner<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMoleRunnerSpawner.class);
    private static final String AGENT_RUNNER_MAIN_CLASS = GenericMoleRunner.class.getName();
    private static final String CURRENT_CLASSPATH_VALUE = System.getProperty("java.class.path");

    @Override
    public Void spawnMoleRunner(JdiMission jdiMission, String... args) throws IOException {
        return spawnMoleRunner(jdiMission, CURRENT_CLASSPATH_VALUE, args);
    }

    @Override
    public Void spawnMoleRunner(JdiMission jdiMission, String classpath, String... args) throws IOException {
        if (null == jdiMission) {
            throw new IllegalArgumentException("The mission must not be null");
        }
        if(null == classpath) {
            throw new IllegalArgumentException("The classpath cannot be null");
        }

        if(args != null && Arrays.asList(args).stream().anyMatch(arg -> null == arg)) {
            throw new IllegalArgumentException("Arguments elements cannot be null");
        }


        ArrayList<String> argsList = new ArrayList<>();
        if(args != null) {
            argsList.addAll(Arrays.asList(args));
        }
        argsList.add(jdiMission.getMoleClassName());
        argsList.add(jdiMission.getMissionContentClassName());

        ProcessBuilder processBuilder = JvmSpawnHelper.getProcessBuilder(
                JvmSpawnHelper.appendToolsJarToClasspath(classpath),
                AGENT_RUNNER_MAIN_CLASS,
                argsList.toArray(new String[argsList.size()]));

        processBuilder.inheritIO().start();
        return null;
    }
}
