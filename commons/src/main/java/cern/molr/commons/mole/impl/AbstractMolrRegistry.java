package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mole.MissionsDiscoverer;
import cern.molr.commons.mole.MolrRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract implementation of the {@link MolrRegistry} that searches for {@link cern.molr.commons.domain.Mission}s when
 * it is being constructed using a provided {@link cern.molr.commons.mole.MissionsDiscoverer}
 *
 * @author tiagomr
 */
public abstract class AbstractMolrRegistry implements MolrRegistry {

    private final Set<Mission> missions = new HashSet<>();

    public AbstractMolrRegistry(MissionsDiscoverer missionsDiscoverer) {
        registerMissions(missionsDiscoverer.availableMissions());
    }

    @Override
    public Set<Mission> getMissions() {
        return new HashSet<>(missions);
    }

    @Override
    public Set<Mission> getMissions(Predicate<Mission> predicate) {
        return missions.stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public void registerMission(Mission mission) {
        this.missions.add(mission);
    }

    @Override
    public void registerMissions(Set<Mission> missions) {
        this.missions.addAll(missions);
    }
}
