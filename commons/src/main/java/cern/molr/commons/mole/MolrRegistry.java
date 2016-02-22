package cern.molr.commons.mole;

import cern.molr.commons.domain.Mission;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Registry that allows for easy registration and fetch of {@link Mission}s
 *
 * @author tiagomr
 */
public interface MolrRegistry {

    /**
     * Fetches all the registered {@link Mission}s
     *
     * @return a {@link Set} with all the registered {@link Mission}s
     */
    public Set<Mission> getMissions();

    /**
     * Fetches all the registered {@link Mission}s filtered by a {@link Predicate}
     *
     * @param predicate {@link Predicate} used to filter the returned {@link Mission}s
     * @return A {@link Set} of filtered {@link Mission}s
     */
    public Set<Mission> getMissions(Predicate<Mission> predicate);

    /**
     * Registers a {@link Mission}
     *
     * @param mission {@link Mission} to be registered
     */
    public void registerMission(Mission mission);

    /**
     * Registers a {@link Set} of {@link Mission}s
     *
     * @param missions {@link Set} of {@link Mission}s to be registered
     */
    public void registerMissions(Set<Mission> missions);
}
