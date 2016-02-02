/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.jvm;

/**
 * Interface that is responsible for spawning JVMs executing cern.jarrace.agent.AgentRegistry#main
 *
 * @author tiagomr
 */
public interface AgentRegistrySpawner {
    void spawnAgentRegistry(String containerName, String jarPath) throws Exception;
}
