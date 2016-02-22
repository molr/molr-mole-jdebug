package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests the behaviour of {@link InMemoryMolrRegistry} class.
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryMolrRegistryTest {

    public static final String MISSION_CONTENT_CLASS_NAME = "MissionContentClassName";
    @Mock
    private Mission mission1;
    @Mock
    private Mission mission2;
    @Mock
    private Mission mission3;
    private Set<Mission> allMissions;
    private InMemoryMolrRegistry inMemoryMolrRegistry;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        inMemoryMolrRegistry = new InMemoryMolrRegistry();
        mockMission(mission1, 1);
        mockMission(mission2, 2);
        mockMission(mission3, 3);
        allMissions = new HashSet<>(Arrays.asList(mission1, mission2, mission3));
    }

    private void mockMission(Mission mission, int missionNumber) {
        when(mission.getMissionContentClassName()).thenReturn(MISSION_CONTENT_CLASS_NAME + missionNumber);
    }

    @Test
    public void testContructWithNullDiscoverer() {
        expectedException.expect(IllegalArgumentException.class);
        new InMemoryMolrRegistry(null);
    }

    @Test
    public void testRegisterMissionWithNullValue() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryMolrRegistry.registerMission(null);
    }

    @Test
    public void testRegisterMission() {
        inMemoryMolrRegistry.registerMission(mission1);
        Set<Mission> missions = inMemoryMolrRegistry.getMissions();
        assertEquals(1, missions.size());
    }

    @Test
    public void testRegisterMissionsWithNullValue() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryMolrRegistry.registerMissions(null);
    }

    @Test
    public void testRegisterMissionsWithNullElements() {
        expectedException.expect(IllegalArgumentException.class);
        HashSet<Mission> missions = new HashSet<>();
        missions.add(null);
        inMemoryMolrRegistry.registerMissions(missions);
    }

    @Test
    public void testRegisterMissions() {
        inMemoryMolrRegistry.registerMissions(allMissions);
        assertEquals(3, inMemoryMolrRegistry.getMissions().size());
    }

    @Test
    public void testGetMissionsWithZeroMissions() throws Exception {
        assertEquals(0, inMemoryMolrRegistry.getMissions().size());
    }

    @Test
    public void testGetMissionsWithOneMission() throws Exception {
        inMemoryMolrRegistry.registerMission(mission1);
        assertEquals(1, inMemoryMolrRegistry.getMissions().size());
    }

    @Test
    public void testGetMissionsWithManyMissions() throws Exception {
        inMemoryMolrRegistry.registerMissions(allMissions);
        assertEquals(3, inMemoryMolrRegistry.getMissions().size());
    }

    @Test
    public void testGetFilteredMissionsWithZeroMissions() throws Exception {
        Set<Mission> missions = inMemoryMolrRegistry.getMissions(
                mission -> MISSION_CONTENT_CLASS_NAME.equals(mission.getMissionContentClassName()));
        assertEquals(0, missions.size());
    }

    @Test
    public void testGetFilteredMissionsWithOneMission() throws Exception {
        inMemoryMolrRegistry.registerMission(mission1);
        Set<Mission> missions = inMemoryMolrRegistry.getMissions(
                mission -> mission.getMissionContentClassName().equals(MISSION_CONTENT_CLASS_NAME + 1));
        assertEquals(1, missions.size());
    }

    @Test
    public void testGetFilteredMissionsWithManyMissions() throws Exception {
        inMemoryMolrRegistry.registerMissions(allMissions);
        Set<Mission> missions = inMemoryMolrRegistry.getMissions(
                mission -> mission.getMissionContentClassName().equals(MISSION_CONTENT_CLASS_NAME + 1));
        assertEquals(1, missions.size());
    }
}