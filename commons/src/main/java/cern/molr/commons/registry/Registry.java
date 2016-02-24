package cern.molr.commons.registry;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Registry that allows for easy registration and fetch of {@link T}s
 *
 * @author tiagomr
 */
public interface Registry<T> {

    /**
     * Fetches all the registered {@link T}s
     *
     * @return a {@link Set} with all the registered {@link T}s
     */
    public Set<T> getEntries();

    /**
     * Fetches all the registered {@link T}s filtered by a {@link Predicate}
     *
     * @param predicate {@link Predicate} used to filter the returned {@link T}s
     * @return A {@link Set} of filtered {@link T}s
     */
    public Set<T> getEntries(Predicate<T> predicate);

    /**
     * Registers a {@link T}
     *
     * @param entry {@link T} to be registered
     */
    public void registerEntry(T entry);

    /**
     * Registers a {@link Set} of {@link T}s
     *
     * @param entries {@link Set} of {@link T}s to be registered
     */
    public void registerEntries(Set<T> entries);

    /**
     * Removes a {@link T} from the registry
     *
     * @param entry {@link T} to be removed
     */
    void removeEntry(T entry);

    /**
     * Removes a {@link Set} of {@link T}s from the registry
     *
     * @param entries {@link Set} of {@link T}s to be removed
     */
    void removeEntries(Set<T> entries);
}
