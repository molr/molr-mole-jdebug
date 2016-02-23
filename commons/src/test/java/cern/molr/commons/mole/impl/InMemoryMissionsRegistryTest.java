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
 * Tests the behaviour of {@link InMemoryEntriesRegistry} class.
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryMissionsRegistryTest {

    public static final String MISSION_CONTENT_CLASS_NAME = "MissionContentClassName";
    @Mock
    private Mission mission1;
    @Mock
    private Mission mission2;
    @Mock
    private Mission mission3;
    private Set<Mission> allMissions;
    private InMemoryMissionsRegistry inMemoryMissionsRegistry;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        inMemoryMissionsRegistry = new InMemoryMissionsRegistry();
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
        new InMemoryMissionsRegistry(null);
    }

    @Test
    public void testRegisterMissionWithNullValue() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryMissionsRegistry.registerEntry(null);
    }

    @Test
    public void testRegisterMission() {
        inMemoryMissionsRegistry.registerEntry(mission1);
        Set<Mission> missions = inMemoryMissionsRegistry.getEntries();
        assertEquals(1, missions.size());
    }

    @Test
    public void testRegisterMissionsWithNullValue() {
        expectedException.expect(IllegalArgumentException.class);
        inMemoryMissionsRegistry.registerEntries(null);
    }

    @Test
    public void testRegisterMissionsWithNullElements() {
        expectedException.expect(IllegalArgumentException.class);
        HashSet<Mission> missions = new HashSet<>();
        missions.add(null);
        inMemoryMissionsRegistry.registerEntries(missions);
    }

    @Test
    public void testRegisterMissions() {
        inMemoryMissionsRegistry.registerEntries(allMissions);
        assertEquals(3, inMemoryMissionsRegistry.getEntries().size());
    }

    @Test
    public void testGetMissionsWithZeroMissions() throws Exception {
        assertEquals(0, inMemoryMissionsRegistry.getEntries().size());
    }

    @Test
    public void testGetMissionsWithOneMission() throws Exception {
        inMemoryMissionsRegistry.registerEntry(mission1);
        assertEquals(1, inMemoryMissionsRegistry.getEntries().size());
    }

    @Test
    public void testGetMissionsWithManyMissions() throws Exception {
        inMemoryMissionsRegistry.registerEntries(allMissions);
        assertEquals(3, inMemoryMissionsRegistry.getEntries().size());
    }

    @Test
    public void testGetFilteredMissionsWithZeroMissions() throws Exception {
        Set<Mission> missions = inMemoryMissionsRegistry.getEntries(
                mission -> MISSION_CONTENT_CLASS_NAME.equals(mission.getMissionContentClassName()));
        assertEquals(0, missions.size());
    }

    @Test
    public void testGetFilteredMissionsWithOneMission() throws Exception {
        inMemoryMissionsRegistry.registerEntry(mission1);
        Set<Mission> missions = inMemoryMissionsRegistry.getEntries(
                mission -> mission.getMissionContentClassName().equals(MISSION_CONTENT_CLASS_NAME + 1));
        assertEquals(1, missions.size());
    }

    @Test
    public void testGetFilteredMissionsWithManyMissions() throws Exception {
        inMemoryMissionsRegistry.registerEntries(allMissions);
        Set<Mission> missions = inMemoryMissionsRegistry.getEntries(
                mission -> mission.getMissionContentClassName().equals(MISSION_CONTENT_CLASS_NAME + 1));
        assertEquals(1, missions.size());
    }
}