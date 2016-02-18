package cern.molr.commons.mole;

import cern.molr.commons.domain.Service;

import java.util.List;

/**
 * Interface to be implemented to provide different {@link Service}s.
 * @author timartin
 * @author mgalilee
 */
public interface ServiceDiscoverer {
    List<Service> availableServices();
}
