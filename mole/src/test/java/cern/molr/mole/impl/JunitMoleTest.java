/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.mole.impl;

import cern.molr.TestDefinitions;
import cern.molr.commons.exception.MissionExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class that tests the behaviour of {@link JunitMole}
 *
 * @author tiagomr
 */
public class JunitMoleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private final JunitMole junitMole = new JunitMole();

    @Test
    public void testDiscoverJunitMission() throws NoSuchMethodException {
        List<Method> actualMethods = junitMole.discover(TestDefinitions.JunitMission.class);
        List<Method> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestDefinitions.JunitMission.class.getMethod("mission1"));
        expectedMethods.add(TestDefinitions.JunitMission.class.getMethod("mission2"));
        assertEquals(expectedMethods, actualMethods);
    }

    @Test
    public void testDiscoverNonRunnableMission() {
        List<Method> actualMethods = junitMole.discover(TestDefinitions.EmptyMission.class);
        assertTrue(actualMethods.isEmpty());
    }

    @Test
    public void testDiscoverJunitMissionWithNullClassType() {
        expectedException.expect(IllegalArgumentException.class);
        junitMole.discover(null);
    }

    @Test
    public void testRunJunitMission() {
        junitMole.run(TestDefinitions.JunitMission.class.getName());
    }

    @Test
    public void testRunWithNonJunitMission() {
        junitMole.run(TestDefinitions.EmptyMission.class.getName());
    }

    @Test
    public void testRunWithNullMissionContentClassType() {
        expectedException.expect(MissionExecutionException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        junitMole.run(null);
    }

    @Test
    public void testRunWithNonExistentMissionContentClassType() {
        expectedException.expect(MissionExecutionException.class);
        expectedException.expectCause(isA(ClassNotFoundException.class));
        junitMole.run("NonExistent");
    }
}