/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.registry.impl;

import cern.molr.commons.registry.ObservableRegistry;
import cern.molr.commons.registry.Registry;

import java.util.LinkedList;
import java.util.Set;

/**
 * Extension of {@link InMemoryEntriesRegistry} that implements {@link ObservableRegistry} and allows for
 * {@link ObservableRegistry.OnCollectionChangedListener} to be notified of changes to the
 * entries
 *
 * @param <T> the type of entries being handled by this {@link Registry}
 * @author tiagomr
 */
public class ObservableInMemoryEntriesRegistry<T> extends InMemoryEntriesRegistry<T> implements ObservableRegistry<T> {

    private final LinkedList<OnCollectionChangedListener<T>> listeners = new LinkedList<>();

    @Override
    public boolean registerEntry(T entry) {
        if (super.registerEntry(entry)) {
            notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public boolean registerEntries(Set<T> entries) {
        if (super.registerEntries(entries)) {
            notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEntry(T entry) {
        if (super.removeEntry(entry)) {
            notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEntries(Set<T> entries) {
        if (super.removeEntries(entries)) {
            notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public void addListener(OnCollectionChangedListener<T> listener) {
        listeners.addLast(listener);
    }

    private void notifyListeners() {
        listeners.stream().forEach(listener -> listener.onCollectionChanged(getEntries()));
    }
}
