package cern.molr.registry;

import cern.molr.registry.domain.MoleRegistration;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Registry component that stores information about deployed {@link cern.molr.commons.mole.Mole}s encapsulating that
 * information into {@link MoleRegistration} objects
 * @author timartin
 */
public interface MoleRegistry {

    /**
     * @return All stored {@link MoleRegistration}s
     */
    Set<MoleRegistration> getRegisteredMoles();

    /**
     * @param predicate
     * @return All stored {@link MoleRegistration}s that satisfy the given predicate
     */
    Set<MoleRegistration> getRegisteredMoles(Predicate<MoleRegistration> predicate);

    /**
     * Stores the given {@link MoleRegistration}
     * @param moleRegistration {@link MoleRegistration} to be stored
     * @return True if the {@link MoleRegistration} was successfully stored, false otherwise
     */
    boolean registerMole(MoleRegistration moleRegistration);
}
