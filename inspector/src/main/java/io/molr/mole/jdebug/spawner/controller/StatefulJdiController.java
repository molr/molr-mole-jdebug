package io.molr.mole.jdebug.spawner.controller;

import io.molr.mole.jdebug.spawner.entry.EntryState;

import java.util.Optional;

/**
 * Stateful extension of the {@link JdiController} interface
 *
 * @author mgalilee
 */
public interface StatefulJdiController extends JdiController {
    boolean isDead();

    Optional<EntryState> getLastKnownState();

    void addObserver(JdiStateObserver jdiStateObserver);

    void removeObserver(JdiStateObserver jdiStateObserver);

    default boolean canStep() {
        return !isDead() && getLastKnownState().isPresent();
    }

    interface JdiStateObserver {
        void death();

        void entryStateChanged();
    }
}
