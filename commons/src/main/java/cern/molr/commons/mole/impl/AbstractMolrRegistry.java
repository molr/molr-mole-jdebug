package cern.molr.commons.mole.impl;

import cern.molr.commons.mole.MissionsDiscoverer;
import cern.molr.commons.mole.MolrRegistry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract implementation of the {@link MolrRegistry} that searches for {@link cern.molr.commons.domain.Mission}s when
 * it is being constructed using a provided {@link cern.molr.commons.mole.MissionsDiscoverer}
 *
 * @author tiagomr
 */
public abstract class AbstractMolrRegistry implements MolrRegistry {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(1);
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    public AbstractMolrRegistry(MissionsDiscoverer missionsDiscoverer) {
        registerMissions(missionsDiscoverer.availableMissions());
    }
}
