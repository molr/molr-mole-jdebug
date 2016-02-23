package cern.molr.commons.mole.impl;

import cern.molr.commons.mole.Registry;

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
            throw new IllegalArgumentException("entries values cannot be null");
        }
        this.entries.addAll(entries);
    }

    @Override
    public void removeEntry(T entry) {
        if(entry == null) {
            throw new IllegalArgumentException("entries cannot be null");
        }
        entries.remove(entry);
    }

    @Override
    public void removeEntries(Set<T> entries) {
        if (null == entries) {
            throw new IllegalArgumentException("entries cannot be null");
        }
        if (entries.stream().anyMatch(entry -> null == entry)) {
            throw new IllegalArgumentException("entries values cannot be null");
        }
        entries.removeAll(entries);
    }
}
