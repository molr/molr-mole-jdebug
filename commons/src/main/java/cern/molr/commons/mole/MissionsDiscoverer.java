package cern.molr.commons.mole;

import cern.molr.commons.domain.Mission;

import java.util.List;

/**
 * Interface to be implemented to provide different {@link Mission}s.
 * @author timartin
 * @author mgalilee
 */
public interface MissionsDiscoverer {
    List<Mission> availableMissions();
}
