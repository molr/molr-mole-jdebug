package cern.molr.commons.mole.impl;

import cern.molr.commons.mole.ObservableRegistry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author tiagomr
 */
public class ObservableInMemoryEntriesRegistry<T> extends InMemoryEntriesRegistry<T> implements ObservableRegistry<T>{

    private final LinkedList<OnCollectionChangedListener> listeners = new LinkedList<>();

    @Override
    public void registerEntry(T entry) {
        super.registerEntry(entry);
        listeners.stream().forEach(listener -> listener.onCollectionChanged(getEntries()));
    }

    @Override
    public void registerEntries(Set<T> entries) {
        super.registerEntries(entries);
        listeners.stream().forEach(listener -> listener.onCollectionChanged(getEntries()));
    }

    @Override
    public void addListener(OnCollectionChangedListener listener) {
        listeners.addLast(listener);
    }
}
