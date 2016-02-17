/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.jvm;

import cern.molr.commons.domain.Service;

import java.util.List;

/**
 * Interface that is responsible for spawning JVMs executing cern.jarrace.mole.AgentRunner
 *
 * @author tiagomr
 */
public interface MoleSpawner<T> {
    /**
     * Spawns a mole in a new JVM with the same classpath as the calling JVM
     * @param service
     * @param args
     * @return
     * @throws Exception
     */
    public T spawnMoleRunner(Service service, String... args) throws Exception;

    /**
     * Spawns a mole in a new JVM
     * @param service
     * @param classpath
     * @param args
     * @return
     * @throws Exception
     */
    public T spawnMoleRunner(Service service, String classpath, String... args) throws Exception;
}
