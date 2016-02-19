/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.jvm;

import cern.molr.commons.domain.Mission;

/**
 * Interface that is responsible for spawning JVMs executing cern.jarrace.mole.AgentRunner
 *
 * @author tiagomr
 */
public interface MoleSpawner<T> {
    /**
     * Spawns a mole in a new JVM with the same classpath as the calling JVM
     * @param mission
     * @param args
     * @return
     * @throws Exception
     */
    public T spawnMoleRunner(Mission mission, String... args) throws Exception;

    /**
     * Spawns a mole in a new JVM
     * @param mission
     * @param classpath
     * @param args
     * @return
     * @throws Exception
     */
    public T spawnMoleRunner(Mission mission, String classpath, String... args) throws Exception;
}
