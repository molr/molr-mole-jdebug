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
 * Class that tests the behaviour of {@link RunnableMole}
 *
 * @author tiagomr
 */
public class RunnableSpringMoleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private final RunnableSpringMole runnableSpringMole = new RunnableSpringMole();

    @Test
    public void testDiscoverRunnableSpringMission() throws NoSuchMethodException {
        List<Method> actualMethods = runnableSpringMole.discover(TestDefinitions.RunnableSpringMission.class);
        List<Method> expectedMethods = new ArrayList<>();
        expectedMethods.add(TestDefinitions.RunnableSpringMission.class.getMethod("run"));
        assertEquals(expectedMethods, actualMethods);
    }

    @Test
    public void testDiscoverRunnableNonAnnotatedMission() {
        List<Method> actualMethods = runnableSpringMole.discover(TestDefinitions.RunnableMission.class);
        assertTrue(actualMethods.isEmpty());
    }

    @Test
    public void testDiscoverNonRunnableMission() {
        List<Method> actualMethods = runnableSpringMole.discover(TestDefinitions.EmptyMission.class);
        assertTrue(actualMethods.isEmpty());
    }

    @Test
    public void testDiscoverRunnableMissionWithNullClassType() {
        expectedException.expect(IllegalArgumentException.class);
        runnableSpringMole.discover(null);
    }

    @Test
    public void testRunRunnableSpringMission() {
        runnableSpringMole.run(TestDefinitions.RunnableSpringMission.class.getName());
    }

    @Test
    public void testRunWithRunnableNonAnnotatedMission() {
        expectedException.expect(MissionExecutionException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        runnableSpringMole.run(TestDefinitions.RunnableMission.class.getName());
    }

    @Test
    public void testRunWithNonRunnableMission() {
        expectedException.expect(MissionExecutionException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        runnableSpringMole.run(TestDefinitions.EmptyMission.class.getName());
    }

    @Test
    public void testRunWithNullMissionContentClassType() {
        expectedException.expect(MissionExecutionException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        runnableSpringMole.run(null);
    }

    @Test
    public void testRunWithNonExistentMissionContentClassType() {
        expectedException.expect(MissionExecutionException.class);
        expectedException.expectCause(isA(ClassNotFoundException.class));
        runnableSpringMole.run("NonExistent");
    }
}