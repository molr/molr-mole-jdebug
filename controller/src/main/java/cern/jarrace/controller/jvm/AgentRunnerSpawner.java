package cern.jarrace.controller.jvm;

import cern.jarrace.commons.domain.Service;

/**
 * Created by timartin on 28/01/2016.
 */
public interface AgentRunnerSpawner {
    public String spawnAgentRunner(Service service, String jarPath, String... args) throws Exception;
}
