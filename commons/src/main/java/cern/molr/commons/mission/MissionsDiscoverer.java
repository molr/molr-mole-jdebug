/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.mission;

import cern.molr.commons.domain.Mission;

import java.util.Set;

/**
 * Interface that provides a way to discover {@link Mission}s
 *
 * @author timartin
 * @author mgalilee
 */
public interface MissionsDiscoverer {

    /**
     * Searches for available {@link Mission}s
     *
     * @return A {@link Set} of {@link Mission}s
     */
    Set<Mission> availableMissions();
}
