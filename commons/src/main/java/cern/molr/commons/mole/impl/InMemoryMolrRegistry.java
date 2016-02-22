package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mole.MissionsDiscoverer;
import cern.molr.commons.mole.MolrRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link MolrRegistry} that searches for {@link cern.molr.commons.domain.Mission}s when
 * it is being constructed using a provided {@link cern.molr.commons.mole.MissionsDiscoverer} and stores them into
 * memory.
 *
 * @author tiagomr
 */
public class InMemoryMolrRegistry implements MolrRegistry {

    private final Set<Mission> missions = new HashSet<>();

    public InMemoryMolrRegistry() {}

    public InMemoryMolrRegistry(MissionsDiscoverer missionsDiscoverer) {
        if(null == missionsDiscoverer) {
            throw new IllegalArgumentException("missions discoverer cannot be null");
        }
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
        if(null == mission) {
            throw new IllegalArgumentException("mission cannot be null");
        }
        this.missions.add(mission);
    }

    @Override
    public void registerMissions(Set<Mission> missions) {
        if(null == missions) {
            throw new IllegalArgumentException("missions cannot be null");
        }
        if(missions.stream().anyMatch(mission -> null == mission)) {
            throw new IllegalArgumentException("missions values cannot be null");
        }
        this.missions.addAll(missions);
    }
}
