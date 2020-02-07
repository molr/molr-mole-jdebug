/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.jvm;

import cern.molr.commons.domain.JdiMission;

/**
 * Interface that is responsible for spawning JVMs executing cern.jarrace.mole.AgentRunner
 *
 * @author tiagomr
 */
public interface MoleSpawner<T> {
    /**
     * Spawns a mole in a new JVM with the same classpath as the calling JVM
     *
     * @param jdiMission
     * @param args
     * @return
     * @throws Exception
     */
    T spawnMoleRunner(JdiMission jdiMission, String... args) throws Exception;

    /**
     * Spawns a mole in a new JVM
     *
     * @param service
     * @param classpath
     * @param args
     * @return
     * @throws Exception
     */
    T spawnMoleRunner(JdiMission service, String classpath, String... args) throws Exception;
}
