/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.registry.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mission.MissionsDiscoverer;
import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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
    private Mission mission1;
    @Mock
    private Mission mission2;
    private Set<Mission> testMissions;
    @Mock
    private MissionsDiscoverer missionsDiscoverer;
    private InMemoryMissionsRegistry inMemoryMissionsRegistry;

    @Before
    public void setUp() {
        testMissions = new HashSet<>(Arrays.asList(mission1, mission2));
    }

    @Test
    public void testInstantiateInMemoryMissionsRegistry() throws NoSuchFieldException {
        Mockito.when(missionsDiscoverer.availableMissions()).thenReturn(testMissions);
        inMemoryMissionsRegistry = new InMemoryMissionsRegistry(missionsDiscoverer);
        @SuppressWarnings("unchecked")
        Set<Mission> missions = (Set<Mission>) PrivateAccessor.getField(inMemoryMissionsRegistry, ENTRIES_PRIVATE_FIELD_NAME);
        assertEquals(2, missions.size());
        assertEquals(testMissions, missions);
    }
}