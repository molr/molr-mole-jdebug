/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.mission;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.exception.MissionMaterializationException;

/**
 * Interface that provides a way to instantiate {@link Mission}s
 *
 * @author tiagomr
 */
public interface MissionMaterializer {

    /**
     * Tries to instantiate a mission from a given {@link Class}
     *
     * @param classType {@link Class} from which the {@link Mission} will be generated
     * @return A {@link Mission}
     * @see Mission
     */
    Mission materialize(Class<?> classType) throws MissionMaterializationException;
}
