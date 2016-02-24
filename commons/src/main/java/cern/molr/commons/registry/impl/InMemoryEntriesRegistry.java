package cern.molr.commons.registry.impl;

import cern.molr.commons.registry.Registry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link Registry} interface that stores the entries in memory only.
 * This implementation does not provide any persistence support and so all the information is lost once the instance
 * is deleted.
 *
 * @author tiagomr
 */
public class InMemoryEntriesRegistry<T> implements Registry<T> {

    protected final Set<T> entries = new HashSet<>();

    public InMemoryEntriesRegistry() {
    }

    @Override
    public Set<T> getEntries() {
        return new HashSet<>(entries);
    }

    @Override
    public Set<T> getEntries(Predicate<T> predicate) {
        return entries.stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public void registerEntry(T entry) {
        if (null == entry) {
            throw new IllegalArgumentException("entry cannot be null");
        }
        this.entries.add(entry);
    }

    @Override
    public void registerEntries(Set<T> entries) {
        if (null == entries) {
            throw new IllegalArgumentException("entries cannot be null");
        }
        if (entries.stream().anyMatch(entry -> null == entry)) {
            throw new IllegalArgumentException("no entries value can be null");
        }
        this.entries.addAll(entries);
    }

    @Override
    public void removeEntry(T entryToRemove) {
        if(entryToRemove == null) {
            throw new IllegalArgumentException("entryToRemove cannot be null");
        }
        entries.remove(entryToRemove);
    }

    @Override
    public void removeEntries(Set<T> entriesToRemove) {
        if (null == entriesToRemove) {
            throw new IllegalArgumentException("entriesToRemove cannot be null");
        }
        if (entriesToRemove.stream().anyMatch(entry -> null == entry)) {
            throw new IllegalArgumentException("no entriesToRemove value can be null");
        }
        this.entries.removeAll(entriesToRemove);
    }
}
