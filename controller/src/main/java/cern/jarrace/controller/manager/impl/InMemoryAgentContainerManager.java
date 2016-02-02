/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.manager.impl;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.controller.manager.AgentContainerManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link AgentContainerManager} that stores the information in an "in memory" set
 *
 * @author tiagomr
 */
public class InMemoryAgentContainerManager implements AgentContainerManager {

    private final Set<AgentContainer> agentContainers = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Optional<AgentContainer> findAgentContainer(String containerName) {
        if (containerName == null || containerName.isEmpty()) {
            throw new IllegalArgumentException("Name of the container cannot be null nor empty");
        }
        synchronized (agentContainers) {
            Optional<AgentContainer> toReturn = agentContainers.stream().filter(agentContainer -> {
                return containerName.equals(agentContainer.getContainerName()) ? true : false;
            }).findFirst();
            return toReturn;
        }
    }

    @Override
    public Set<AgentContainer> findAllAgentContainers() {
        return new HashSet<>(agentContainers);
    }

    @Override
    public void registerAgentContainer(AgentContainer agentContainer) {
        if (agentContainer == null) {
            throw new IllegalArgumentException("AgentContainer cannot be null");
        }
        if (agentContainer.getContainerName() == null || agentContainer.getContainerName().isEmpty()) {
            throw new IllegalArgumentException("AgentContainer name cannot be null nor empty");
        }
        if (agentContainers.contains(agentContainer)) {
            agentContainers.remove(agentContainer);
        }
        agentContainers.add(agentContainer);
    }

    // Used for testing
    Set<AgentContainer> getAgentContainers() {
        return agentContainers;
    }
}
