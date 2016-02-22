package cern.molr.commons.mole.impl;

import cern.molr.commons.mole.MissionsDiscoverer;
import cern.molr.commons.mole.MolrRegistry;

/**
 * Abstract implementation of the {@link MolrRegistry} that searches for {@link cern.molr.commons.domain.Mission}s when
 * it is being constructed using a provided {@link cern.molr.commons.mole.MissionsDiscoverer}
 *
 * @author tiagomr
 */
public abstract class AbstractMolrRegistry implements MolrRegistry {

    public AbstractMolrRegistry(MissionsDiscoverer missionsDiscoverer) {
        registerMissions(missionsDiscoverer.availableMissions());
    }
}
