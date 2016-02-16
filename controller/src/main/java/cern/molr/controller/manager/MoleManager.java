/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.manager;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.commons.domain.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Class that manages {@link MoleContainer}s providing ways of registering and fetching them
 *
 * @author tiagomr
 */
public interface MoleManager {

    /**
     * Searches for a specific {@link MoleContainer} by name
     *
     * @param moleName
     * @return An {@link Optional} of {@link MoleContainer} with the specified name
     */
    Optional<MoleContainer> getMole(String moleName);

    /**
     * Provides a {@link List} with all the registered {@link MoleContainer}s
     *
     * @return {@link List} of {@link MoleContainer}s
     */
    Set<MoleContainer> getAllMoles();

    /**
     * Registers a {@link MoleContainer} that will be expose its {@link Service}s
     *
     * @param moleContainer The {@link MoleContainer} to be registered
     */
    void registerMole(MoleContainer moleContainer);
}
