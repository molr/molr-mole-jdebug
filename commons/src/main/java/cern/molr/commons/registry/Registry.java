/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

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
    Set<T> getEntries();

    /**
     * Fetches all the registered {@link T}s filtered by a {@link Predicate}
     *
     * @param predicate {@link Predicate} used to filter the returned {@link T}s
     * @return A {@link Set} of filtered {@link T}s
     */
    Set<T> getEntries(Predicate<T> predicate);

    /**
     * Registers a {@link T}
     *
     * @param entry {@link T} to be registered
     * @return True if the {@link T} as been registered, false otherwise
     */
    boolean registerEntry(T entry);

    /**
     * Registers a {@link Set} of {@link T}s
     *
     * @param entries {@link Set} of {@link T}s to be registered
     * @return True if the {@link Registry} has been updated by any element of the given {@link Set}, false otherwise
     */
    boolean registerEntries(Set<T> entries);

    /**
     * Removes a {@link T} from the registry
     *
     * @param entry {@link T} to be removed
     * @return True if the {@link T} as been removed, false otherwise
     */
    boolean removeEntry(T entry);

    /**
     * Removes a {@link Set} of {@link T}s from the registry
     *
     * @param entries {@link Set} of {@link T}s to be removed
     * @return True if the {@link Registry} has been updated by any element of the given {@link Set}, false otherwise
     */
    boolean removeEntries(Set<T> entries);
}
