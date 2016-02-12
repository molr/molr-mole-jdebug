/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.manager.impl;

import cern.molr.commons.domain.Mole;
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

    private final Set<Mole> moles = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Optional<Mole> getMole(String moleName) {
        if (moleName == null || moleName.isEmpty()) {
            throw new IllegalArgumentException("Name of the container cannot be null nor empty");
        }
        synchronized (moles) {
            Optional<Mole> toReturn = moles.stream().filter(agentContainer -> {
                return moleName.equals(agentContainer.getContainerName()) ? true : false;
            }).findFirst();
            return toReturn;
        }
    }

    @Override
    public Set<Mole> getAllMoles() {
        return new HashSet<>(moles);
    }

    @Override
    public void registerMole(Mole mole) {
        if (mole == null) {
            throw new IllegalArgumentException("Mole cannot be null");
        }
        if (mole.getContainerName() == null || mole.getContainerName().isEmpty()) {
            throw new IllegalArgumentException("Mole name cannot be null nor empty");
        }
        if (moles.contains(mole)) {
            moles.remove(mole);
        }
        moles.add(mole);
    }

    // Used for testing
    Set<Mole> getMoles() {
        return moles;
    }
}
