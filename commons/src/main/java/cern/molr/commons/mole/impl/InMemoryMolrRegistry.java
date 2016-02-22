package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mole.MissionsDiscoverer;
import cern.molr.commons.mole.MolrRegistry;

/**
 * Implementation of {@link MolrRegistry} that stores {@link Mission}s in memory only.
 * This implementation does not provide state persistence features, meaning that all the registrations are lost on
 * restart.
 *
 * @author timartin
 */
public class InMemoryMolrRegistry extends AbstractMolrRegistry {

    public InMemoryMolrRegistry() {
        super(new ClasspathAnnotatedMissionDiscoverer());
    }

    public InMemoryMolrRegistry(MissionsDiscoverer missionsDiscoverer) {
        super(missionsDiscoverer);
    }
}
