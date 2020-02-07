/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.registry.impl;

import cern.molr.commons.domain.JdiMission;
import cern.molr.commons.mission.MissionsDiscoverer;
import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Class that tests the behaviour of {@link InMemoryMissionsRegistry}
 *
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryMissionsRegistryTest {

    private static final String ENTRIES_PRIVATE_FIELD_NAME = "entries";

    @Mock
    private JdiMission jdiMission1;
    @Mock
    private JdiMission jdiMission2;
    private Set<JdiMission> testJdiMissions;
    @Mock
    private MissionsDiscoverer missionsDiscoverer;
    private InMemoryMissionsRegistry inMemoryMissionsRegistry;

    @Before
    public void setUp() {
        testJdiMissions = new HashSet<>(Arrays.asList(jdiMission1, jdiMission2));
    }

    @Test
    public void testInstantiateInMemoryMissionsRegistry() throws NoSuchFieldException {
        Mockito.when(missionsDiscoverer.availableMissions()).thenReturn(testJdiMissions);
        inMemoryMissionsRegistry = new InMemoryMissionsRegistry(missionsDiscoverer);
        @SuppressWarnings("unchecked")
        Set<JdiMission> jdiMissions = (Set<JdiMission>) PrivateAccessor.getField(inMemoryMissionsRegistry, ENTRIES_PRIVATE_FIELD_NAME);
        assertEquals(2, jdiMissions.size());
        assertEquals(testJdiMissions, jdiMissions);
    }
}