/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.registry.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mission.MissionsDiscoverer;

/**
 * In memory {@link Mission} registry that searches for {@link Mission}s on the construction phase using a
 * {@link MissionsDiscoverer}.
 *
 * @see ObservableInMemoryEntriesRegistry
 */
public class InMemoryMissionsRegistry extends ObservableInMemoryEntriesRegistry<Mission> {

    public InMemoryMissionsRegistry() {
    }

    public InMemoryMissionsRegistry(MissionsDiscoverer missionsDiscoverer) {
        super();
        if (null == missionsDiscoverer) {
            throw new IllegalArgumentException("missions discoverer cannot be null");
        }
        registerEntries(missionsDiscoverer.availableMissions());
    }
}
