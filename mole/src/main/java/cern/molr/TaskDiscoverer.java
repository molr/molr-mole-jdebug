package cern.molr;

import cern.molr.mole.Mole;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Interface to be implemented to provide different {@link Mole} discovery mechanisms.
 * @author timartin
 */
public interface TaskDiscoverer {

    /**
     *
     * @return
     */
    public Map<Mole, Map<Class<?>, List<Method>>> discover();
}
