package cern.molr.commons.mole.impl;

import cern.molr.commons.mole.ObservableRegistry;

import java.util.LinkedList;
import java.util.Set;

/**
 * Extension of {@link InMemoryEntriesRegistry} that implements {@link ObservableRegistry} and allows for
 * {@link cern.molr.commons.mole.ObservableRegistry.OnCollectionChangedListener} to be notified of changes to the
 * entries
 *
 * @param <T> the type of entries being handled by this {@link cern.molr.commons.mole.Registry}
 * @author tiagomr
 */
public class ObservableInMemoryEntriesRegistry<T> extends InMemoryEntriesRegistry<T> implements ObservableRegistry<T> {

    private final LinkedList<OnCollectionChangedListener> listeners = new LinkedList<>();

    @Override
    public void registerEntry(T entry) {
        super.registerEntry(entry);
        notifyListeners();
    }

    @Override
    public void registerEntries(Set<T> entries) {
        super.registerEntries(entries);
        notifyListeners();
    }

    @Override
    public void removeEntry(T entry) {
        super.removeEntry(entry);
        notifyListeners();
    }

    @Override
    public void removeEntries(Set<T> entries) {
        super.removeEntries(entries);
        notifyListeners();
    }

    @Override
    public void addListener(OnCollectionChangedListener listener) {
        listeners.addLast(listener);
    }

    private void notifyListeners() {
        listeners.stream().forEach(listener -> listener.onCollectionChanged(getEntries()));
    }
}
