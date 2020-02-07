package io.molr.mole.jdebug.spawner.domain.impl;

import cern.molr.commons.registry.impl.ObservableInMemoryEntriesRegistry;
import io.molr.mole.jdebug.spawner.controller.StatefulJdiController;
import io.molr.mole.jdebug.spawner.domain.Session;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link ObservableInMemoryEntriesRegistry} for {@link Session}s that subscribe to the
 * {@link StatefulJdiController} of its entries
 * @author mgalilee
 */
public class SessionRegistry extends ObservableInMemoryEntriesRegistry<Session> implements StatefulJdiController.JdiStateObserver {

    @Override
    public boolean registerEntry(Session entry) {
        entry.getController().addObserver(this);
        return super.registerEntry(entry);
    }

    @Override
    public boolean registerEntries(Set<Session> entries) {
        entries.forEach(session -> session.getController().addObserver(this));
        return super.registerEntries(entries);
    }

    @Override
    public boolean removeEntry(Session entry) {
        entry.getController().removeObserver(this);
        return super.removeEntry(entry);
    }

    @Override
    public boolean removeEntries(Set<Session> entries) {
        entries.forEach(session -> session.getController().removeObserver(this));
        return super.removeEntries(entries);
    }

    @Override
    public void death() {
        Set<Session> toRemove = getEntries().stream()
                .filter(session -> session.getController().isDead())
                .collect(Collectors.toSet());
        removeEntries(toRemove);
    }

    @Override
    public void entryStateChanged() {
        // does not matter here
    }


}