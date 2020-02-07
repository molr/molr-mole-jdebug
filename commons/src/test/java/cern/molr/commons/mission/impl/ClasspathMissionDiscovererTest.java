/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.mission.impl;

import cern.molr.TestDefinitions;
import cern.molr.commons.domain.JdiMission;
import cern.molr.commons.mission.MissionMaterializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Class that tests the behaviour of {@link AnnotatedMissionMaterializer}
 *
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class ClasspathMissionDiscovererTest {

    @Mock
    private JdiMission jdiMission1;
    @Mock
    private JdiMission jdiMission2;
    @Mock
    private JdiMission jdiMission3;
    @Mock
    private MissionMaterializer missionMaterializer;
    private List<JdiMission> allJdiMission;
    private ClasspathMissionDiscoverer classpathMissionDiscoverer;

    @Before
    public void setUp() {
        when(missionMaterializer.materialize(TestDefinitions.TestMission1.class)).thenReturn(jdiMission1);
        when(missionMaterializer.materialize(TestDefinitions.TestMission2.class)).thenReturn(jdiMission2);
        when(missionMaterializer.materialize(TestDefinitions.TestMission3.class)).thenReturn(jdiMission3);
        allJdiMission = new ArrayList<>(Arrays.asList(jdiMission1, jdiMission2, jdiMission3));
        classpathMissionDiscoverer = new ClasspathMissionDiscoverer(missionMaterializer);
    }

    @Test
    public void testAvailableMissions() {
        Set<JdiMission> jdiMissions = classpathMissionDiscoverer.availableMissions();
        assertEquals(TestDefinitions.NUMBER_OF_TEST_MISSION_DEFINITIONS, jdiMissions.size());
        assertTrue(allJdiMission.stream().allMatch(jdiMissions::contains));
    }
}