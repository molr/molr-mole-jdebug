package cern.molr.commons.mole;

import cern.molr.commons.domain.Mission;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Registry that allows for easy registration and fetch of {@link T}s
 *
 * @author tiagomr
 */
public interface Registry<T> {

    /**
     * Fetches all the registered {@link Mission}s
     *
     * @return a {@link Set} with all the registered {@link Mission}s
     */
    public Set<T> getEntries();

    /**
     * Fetches all the registered {@link T}s filtered by a {@link Predicate}
     *
     * @param predicate {@link Predicate} used to filter the returned {@link T}s
     * @return A {@link Set} of filtered {@link Mission}s
     */
    public Set<T> getEntries(Predicate<T> predicate);

    /**
     * Registers a {@link T}
     *
     * @param object {@link T} to be registered
     */
    public void registerEntry(T object);

    /**
     * Registers a {@link Set} of {@link T}s
     *
     * @param objects {@link Set} of {@link T}s to be registered
     */
    public void registerEntries(Set<T> objects);
}
