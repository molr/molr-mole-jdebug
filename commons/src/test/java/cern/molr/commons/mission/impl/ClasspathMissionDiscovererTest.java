package cern.molr.commons.mission.impl;

import cern.molr.TestDefinitions;
import cern.molr.commons.domain.Mission;
import cern.molr.commons.mission.MissionMaterializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Class that tests the behaviour of {@link AnnotatedMissionMaterializer}
 *
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class ClasspathMissionDiscovererTest {

    @Mock
    private Mission mission1;
    @Mock
    private Mission mission2;
    @Mock
    private Mission mission3;
    @Mock
    private MissionMaterializer missionMaterializer;
    private List<Mission> allMission;
    private ClasspathMissionDiscoverer classpathMissionDiscoverer;

    @Before
    public void setUp() {
        when(missionMaterializer.materialize(TestDefinitions.TestMission1.class)).thenReturn(mission1);
        when(missionMaterializer.materialize(TestDefinitions.TestMission2.class)).thenReturn(mission2);
        when(missionMaterializer.materialize(TestDefinitions.TestMission3.class)).thenReturn(mission3);
        allMission = new ArrayList<>(Arrays.asList(mission1, mission2, mission3));
        classpathMissionDiscoverer = new ClasspathMissionDiscoverer(missionMaterializer);
    }

    @Test
    public void testAvailableMissions() {
        Set<Mission> missions = classpathMissionDiscoverer.availableMissions();
        assertEquals(TestDefinitions.NUMBER_OF_TEST_MISSION_DEFINITIONS, missions.size());
        assertTrue(allMission.stream().allMatch(missions::contains));
    }
}