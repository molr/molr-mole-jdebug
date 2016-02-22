package cern.molr.commons.mole;

import cern.molr.commons.domain.Mission;

import java.util.List;
import java.util.Set;

/**
 * Interface to be implemented to provide different {@link Mission}s.
 * @author timartin
 * @author mgalilee
 */
public interface MissionsDiscoverer {
    Set<Mission> availableMissions();
}
