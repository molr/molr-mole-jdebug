/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.mole;

import cern.molr.commons.domain.Mission;

import java.util.Optional;

/**
 * Interface that provides a ways to instantiate {@link Mission}s
 *
 * @author tiagomr
 */
public interface MissionMaterializer {

    /**
     * Tries to instantiate a mission from the given {@link Class}
     *
     * @param classType {@link Class} from which the {@link Mission} will be generated
     * @return An {@link Optional#of(Object)} of the {@link Mission} when the instantiation is successful,
     * {@link Optional#empty()} otherwise
     * @see Mission
     * @see Mole
     * @see Optional
     */
    Optional<Mission> materialize(Class<?> classType);
}
