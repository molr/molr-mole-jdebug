/*
 * � Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file �COPYING�.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.jvm.impl;

import cern.molr.commons.domain.JdiMission;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

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
    private JdiMission jdiMission;
    private final SimpleMoleRunnerSpawner simpleMoleRunnerSpawner = new SimpleMoleRunnerSpawner();

    @Test
    public void testSpawnMoleRunnerWithNullMission() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        simpleMoleRunnerSpawner.spawnMoleRunner(null, "", (String[]) null);
    }

    @Test
    public void testSpawnMoleRunnerWithNullClasspath() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        simpleMoleRunnerSpawner.spawnMoleRunner(jdiMission, null, (String[]) null);
    }

    @Test
    public void testSpawnMoleRunnerWithNullArgsElements() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        String args[] = {null, null};
        simpleMoleRunnerSpawner.spawnMoleRunner(jdiMission, "", args);
    }

    @Test
    public void testSpawnMoleRunner() throws IOException {
        when(jdiMission.getMoleClassName()).thenReturn("MoleClassName");
        when(jdiMission.getMissionContentClassName()).thenReturn("MissionContentClassName");
        simpleMoleRunnerSpawner.spawnMoleRunner(jdiMission);
    }
}