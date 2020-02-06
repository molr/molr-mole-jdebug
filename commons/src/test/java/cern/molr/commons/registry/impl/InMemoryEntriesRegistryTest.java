/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.registry.impl;

import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Class that tests the behaviour of {@link InMemoryEntriesRegistryTest}
 *
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryEntriesRegistryTest {

    private static final String ENTRIES_PRIVATE_FIELD_NAME = "entries";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private Object testObject1;
    @Mock
    private Object testObject2;
    @Mock
    private Object testObject3;
    @Mock
    private Object testObject4;
    @Mock
    private Object testObject5;
    private final Set<Object> entries = new HashSet<>();
    private final InMemoryEntriesRegistry<Object> inMemoryEntriesRegistry = new InMemoryEntriesRegistry<>();

    @Before
    public void setUp() throws NoSuchFieldException {
        entries.add(testObject1);
        entries.add(testObject2);
        entries.add(testObject3);
        setInternalState();
    }

    @Test
    public void testGetEntries() {
        Set<Object> inMemoryEntriesRegistryEntries = inMemoryEntriesRegistry.getEntries();
        assertTrue(entries != inMemoryEntriesRegistryEntries);
        assertEquals(entries, inMemoryEntriesRegistryEntries);
        assertEquals(3, inMemoryEntriesRegistryEntries.size());
    }

    @Test
    public void testGetFilteredEntries() {
        Set<Object> inMemoryEntriesRegistryEntries = inMemoryEntriesRegistry.getEntries(testObject1::equals);
        assertEquals(1, inMemoryEntriesRegistryEntries.size());
    }

    @Test
    public void testRegisterEntry() {
        assertTrue(inMemoryEntriesRegistry.registerEntry(testObject4));
        assertTrue(entries.contains(testObject4));
        assertEquals(4, entries.size());
    }

    @Test
    public void testRegisterEntryWithNull() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryEntriesRegistry.registerEntries(null);
    }

    @Test
    public void testRegisterEntryWithExistentInstance() {
        assertTrue(inMemoryEntriesRegistry.registerEntry(testObject4));
        assertFalse(inMemoryEntriesRegistry.registerEntry(testObject4));
        assertTrue(entries.contains(testObject4));
        assertEquals(4, entries.size());
    }

    @Test
    public void testRegisterEntries() {
        assertTrue(inMemoryEntriesRegistry.registerEntries(new HashSet<>(Arrays.asList(testObject4, testObject5))));
        assertTrue(entries.contains(testObject3));
        assertTrue(entries.contains(testObject4));
        assertEquals(5, entries.size());
    }

    @Test
    public void testRegisterEntriesWithNullSet() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryEntriesRegistry.registerEntries(null);
    }

    @Test
    public void testRegisterEntriesWithNullElements() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryEntriesRegistry.registerEntries(new HashSet<>(Arrays.asList(null, null)));
    }

    @Test
    public void testRegisterEntriesWithExistentInstances() {
        assertTrue(inMemoryEntriesRegistry.registerEntries(new HashSet<>(Arrays.asList(testObject4, testObject5))));
        assertFalse(inMemoryEntriesRegistry.registerEntries(new HashSet<>(Arrays.asList(testObject4, testObject5))));
        assertTrue(entries.contains(testObject4));
        assertTrue(entries.contains(testObject5));
        assertEquals(5, entries.size());
    }

    @Test
    public void testRemoveEntry() {
        assertTrue(inMemoryEntriesRegistry.removeEntry(testObject1));
        assertFalse(entries.contains(testObject1));
        assertEquals(2, entries.size());
    }

    @Test
    public void testRemoveEntryWithNull() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryEntriesRegistry.removeEntry(null);
    }

    @Test
    public void testRemoveEntryWithNonExistentInstance() {
        assertFalse(inMemoryEntriesRegistry.removeEntry(testObject4));
        assertTrue(entries.contains(testObject1));
        assertTrue(entries.contains(testObject2));
        assertTrue(entries.contains(testObject3));
        assertEquals(3, entries.size());
    }

    @Test
    public void testRemoveEntries() {
        assertTrue(inMemoryEntriesRegistry.removeEntries(new HashSet<>(Arrays.asList(testObject1, testObject2))));
        assertFalse(entries.contains(testObject1));
        assertFalse(entries.contains(testObject2));
        assertEquals(1, entries.size());
    }

    @Test
    public void testRemoveEntriesWithNullSet() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryEntriesRegistry.removeEntries(null);
    }

    @Test
    public void testRemoveEntriesWithNullElements() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryEntriesRegistry.removeEntries(new HashSet<>(Arrays.asList(null, null)));
    }

    @Test
    public void testRemoveEntriesWithNonExistentInstances() {
        assertTrue(inMemoryEntriesRegistry.removeEntries(new HashSet<>(Arrays.asList(testObject1, testObject2))));
        assertFalse(inMemoryEntriesRegistry.removeEntries(new HashSet<>(Arrays.asList(testObject1, testObject2))));
        assertFalse(entries.contains(testObject1));
        assertFalse(entries.contains(testObject2));
        assertEquals(1, entries.size());
    }

    private void setInternalState() throws NoSuchFieldException {
        PrivateAccessor.setField(inMemoryEntriesRegistry, ENTRIES_PRIVATE_FIELD_NAME, entries);
    }
}