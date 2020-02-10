package io.molr.mole.jdebug.spawner.remote;

import io.molr.mole.jdebug.spawner.entry.EntryListener;

/**
 * A list of available methods in {@link EntryListener}s.
 */
public enum EntryListenerMethod {
    ON_LOCATION_CHANGE, ON_INSPECTION_END, ON_VM_DEATH;
}
