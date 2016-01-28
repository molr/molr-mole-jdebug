/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.manager.impl;

import cern.jarrace.controller.domain.AgentContainer;
import cern.jarrace.controller.manager.AgentContainerManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link AgentContainerManager} that stores the information in an "in memory" set
 */
public class InMemoryAgentContainerManager implements AgentContainerManager {

    private final Set<AgentContainer> agentContainers = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Set<AgentContainer> getAgentContainers() {
        return new HashSet<>(agentContainers);
    }

    @Override
    public void registerAgentContainer(AgentContainer agentContainer) {
        if(agentContainers.contains(agentContainer)) {
            agentContainers.remove(agentContainer);
        }
        agentContainers.add(agentContainer);
    }
}
