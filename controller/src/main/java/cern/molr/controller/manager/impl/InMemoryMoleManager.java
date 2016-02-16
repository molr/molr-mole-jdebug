/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.manager.impl;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.controller.manager.MoleManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link MoleManager} that stores the information in an "in memory" set
 *
 * @author tiagomr
 */
public class InMemoryMoleManager implements MoleManager {

    private final Set<MoleContainer> moleContainers = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Optional<MoleContainer> getMole(String moleName) {
        if (moleName == null || moleName.isEmpty()) {
            throw new IllegalArgumentException("Name of the container cannot be null nor empty");
        }
        synchronized (moleContainers) {
            Optional<MoleContainer> toReturn = moleContainers.stream().filter(agentContainer -> {
                return moleName.equals(agentContainer.getContainerName()) ? true : false;
            }).findFirst();
            return toReturn;
        }
    }

    @Override
    public Set<MoleContainer> getAllMoles() {
        return new HashSet<>(moleContainers);
    }

    @Override
    public void registerMole(MoleContainer moleContainer) {
        if (moleContainer == null) {
            throw new IllegalArgumentException("MoleContainer cannot be null");
        }
        if (moleContainer.getContainerName() == null || moleContainer.getContainerName().isEmpty()) {
            throw new IllegalArgumentException("MoleContainer name cannot be null nor empty");
        }
        if (moleContainers.contains(moleContainer)) {
            moleContainers.remove(moleContainer);
        }
        moleContainers.add(moleContainer);
    }

    // Used for testing
    Set<MoleContainer> getMoleContainers() {
        return moleContainers;
    }
}
