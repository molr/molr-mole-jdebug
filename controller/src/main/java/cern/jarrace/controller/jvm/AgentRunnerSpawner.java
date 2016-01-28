package cern.jarrace.controller.jvm;

import cern.jarrace.controller.domain.Service;

/**
 * Created by timartin on 28/01/2016.
 */
public interface AgentRunnerSpawner {
    public void spawnAgentContainer(Service service, String jarPath, String... args) throws Exception;
}
