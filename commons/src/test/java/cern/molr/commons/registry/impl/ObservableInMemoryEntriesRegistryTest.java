/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.registry.impl;

import cern.molr.commons.registry.ObservableRegistry;
import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Class that tests the behaviour of {@link InMemoryMissionsRegistry}
 *
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class ObservableInMemoryEntriesRegistryTest {

    private static final String ENTRIES_PRIVATE_FIELD_NAME = "entries";

    @Mock
    private Object testObject1;
    @Mock
    private Object testObject2;
    @Mock
    private Object testObject3;
    @Mock
    private Object testObject4;
    private final Set<Object> entries = new HashSet<>();
    @Mock
    private ObservableRegistry.OnCollectionChangedListener<Object> onCollectionChangedListener;
    private final ObservableInMemoryEntriesRegistry<Object> objectObservableInMemoryEntriesRegistry = new ObservableInMemoryEntriesRegistry<>();

    @Before
    public void setUp() throws NoSuchFieldException {
        entries.add(testObject1);
        entries.add(testObject2);
        objectObservableInMemoryEntriesRegistry.addListener(onCollectionChangedListener);
        setInternalState();
    }

    @Test
    public void testRegisterEntry() {
        objectObservableInMemoryEntriesRegistry.registerEntry(testObject3);
        verify(onCollectionChangedListener).onCollectionChanged(any());
    }

    @Test
    public void testRegisterEntryWithExistentInstance() {
        objectObservableInMemoryEntriesRegistry.registerEntry(testObject1);
        verify(onCollectionChangedListener, times(0)).onCollectionChanged(any());
    }

    @Test
    public void testRegisterEntries() {
        objectObservableInMemoryEntriesRegistry.registerEntries(new HashSet<>(Arrays.asList(testObject3, testObject4)));
        verify(onCollectionChangedListener).onCollectionChanged(any());
    }

    @Test
    public void testRegisterEntriesWithExistentInstances() {
        objectObservableInMemoryEntriesRegistry.registerEntries(new HashSet<>(Arrays.asList(testObject1, testObject2)));
        verify(onCollectionChangedListener, times(0)).onCollectionChanged(any());
    }

    @Test
    public void testRemoveEntry() {
        objectObservableInMemoryEntriesRegistry.removeEntry(testObject1);
        verify(onCollectionChangedListener).onCollectionChanged(any());
    }

    @Test
    public void testRemoveNonExistentEntry() {
        objectObservableInMemoryEntriesRegistry.removeEntry(testObject3);
        verify(onCollectionChangedListener, times(0)).onCollectionChanged(any());
    }

    @Test
    public void testRemoveEntries() {
        objectObservableInMemoryEntriesRegistry.removeEntries(new HashSet<>(Arrays.asList(testObject1, testObject2)));
        verify(onCollectionChangedListener).onCollectionChanged(any());
    }

    @Test
    public void testRemoveNonExistentEntries() {
        objectObservableInMemoryEntriesRegistry.removeEntries(new HashSet<>(Arrays.asList(testObject3, testObject4)));
        verify(onCollectionChangedListener, times(0)).onCollectionChanged(any());
    }

    private void setInternalState() throws NoSuchFieldException {
        PrivateAccessor.setField(objectObservableInMemoryEntriesRegistry, ENTRIES_PRIVATE_FIELD_NAME, entries);
    }
}