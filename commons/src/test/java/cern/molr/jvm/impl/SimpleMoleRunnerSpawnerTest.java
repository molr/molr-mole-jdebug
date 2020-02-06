/*
 * � Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file �COPYING�.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.jvm.impl;

import cern.molr.commons.domain.Mission;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.when;

/**
 * Class that tests the behaviour of {@link SimpleMoleRunnerSpawner}
 *
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleMoleRunnerSpawnerTest extends TestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private Mission mission;
    private final SimpleMoleRunnerSpawner simpleMoleRunnerSpawner = new SimpleMoleRunnerSpawner();

    @Test
    public void testSpawnMoleRunnerWithNullMission() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        simpleMoleRunnerSpawner.spawnMoleRunner(null, "", (String[]) null);
    }

    @Test
    public void testSpawnMoleRunnerWithNullClasspath() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        simpleMoleRunnerSpawner.spawnMoleRunner(mission, null, (String[]) null);
    }

    @Test
    public void testSpawnMoleRunnerWithNullArgsElements() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        String args[] = {null, null};
        simpleMoleRunnerSpawner.spawnMoleRunner(mission, "", args);
    }

    @Test
    public void testSpawnMoleRunner() throws IOException {
        when(mission.getMoleClassName()).thenReturn("MoleClassName");
        when(mission.getMissionContentClassName()).thenReturn("MissionContentClassName");
        simpleMoleRunnerSpawner.spawnMoleRunner(mission);
    }
}