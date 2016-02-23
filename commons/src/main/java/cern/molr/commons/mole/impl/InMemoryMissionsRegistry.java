package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mole.MissionsDiscoverer;

/**
 * In memory {@link Mission} registry that searches for {@link Mission}s on the construction phase using a
 * {@link MissionsDiscoverer}.
 * @see ObservableInMemoryEntriesRegistry
 */
public class InMemoryMissionsRegistry extends ObservableInMemoryEntriesRegistry<Mission> {

    public InMemoryMissionsRegistry() {}

    public InMemoryMissionsRegistry(MissionsDiscoverer missionsDiscoverer) {
        super();
        if(null == missionsDiscoverer) {
            throw new IllegalArgumentException("missions discoverer cannot be null");
        }
        registerEntries(missionsDiscoverer.availableMissions());
    }
}
