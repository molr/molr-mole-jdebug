package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mole.MissionsDiscoverer;
import cern.molr.commons.mole.MolrRegistry;
import cern.molr.commons.mole.impl.AbstractMolrRegistry;
import cern.molr.commons.mole.impl.ClasspathAnnotatedMissionDiscoverer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MolrRegistry} that stores {@link Mission}s in memory only.
 * This implementation does not provide state persistence features, meaning that all the registrations are lost on
 * restart.
 *
 * @author timartin
 */
public class InMemoryMolrRegistry extends AbstractMolrRegistry {

    private final Set<Mission> missions = new HashSet<>();

    public InMemoryMolrRegistry() {
        super(new ClasspathAnnotatedMissionDiscoverer());
    }

    public InMemoryMolrRegistry(MissionsDiscoverer missionsDiscoverer) {
        super(missionsDiscoverer);
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
        missions.add(mission);
    }

    @Override
    public void registerMissions(Set<Mission> missions) {
        missions.addAll(missions);
    }
}
