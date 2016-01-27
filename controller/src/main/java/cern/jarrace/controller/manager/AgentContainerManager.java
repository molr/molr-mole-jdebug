/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.manager;

import cern.jarrace.controller.domain.AgentContainer;
import cern.jarrace.controller.domain.Service;

import java.util.List;
import java.util.Set;

/**
 * Class that manages {@link AgentContainer} providing ways of registering and fetching them
 * @author tiagomr
 */
public interface AgentContainerManager {
    /**
     * Provides a {@link List} with all the registered {@link AgentContainer}s
     * @return {@link List} of {@link AgentContainer}s
     */
    Set<AgentContainer> getAgentContainers();


    /**
     * Registers an {@link AgentContainer} that will be exposed its {@link Service}s
     * @param agentContainer The {@link AgentContainer} to be registered
     */
    void registerAgentContainer(AgentContainer agentContainer);
}
