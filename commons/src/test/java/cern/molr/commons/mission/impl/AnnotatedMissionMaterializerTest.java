/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.mission.impl;

import cern.molr.TestDefinitions;
import cern.molr.commons.domain.Mission;
import cern.molr.commons.exception.MissionMaterializationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Class that tests the behaviour of {@link AnnotatedMissionMaterializer}
 *
 * @author tiagomr
 */
public class AnnotatedMissionMaterializerTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private AnnotatedMissionMaterializer annotatedMissionMaterializer = new AnnotatedMissionMaterializer();

    @Test
    public void testMaterializeWithNullClass() {
        expectedException.expect(MissionMaterializationException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        annotatedMissionMaterializer.materialize(null);
    }

    @Test
    public void testMaterializeWithNotAnnotatedClass() {
        expectedException.expect(MissionMaterializationException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        annotatedMissionMaterializer.materialize(AnnotatedMissionMaterializerTest.class);
    }

    @Test
    public void testMaterializeWithInstantiableMole() {
        expectedException.expect(MissionMaterializationException.class);
        expectedException.expectCause(isA(NoSuchMethodException.class));
        annotatedMissionMaterializer.materialize(TestDefinitions.TestMission1.class);
    }

    @Test
    public void testMaterializeWithMoleWithNoDefaultConstructor() {
        expectedException.expect(MissionMaterializationException.class);
        expectedException.expectCause(isA(NoSuchMethodException.class));
        annotatedMissionMaterializer.materialize(TestDefinitions.TestMission2.class);
    }

    @Test
    public void testMaterialize() {
        Mission materializedMission = annotatedMissionMaterializer.materialize(TestDefinitions.TestMission3.class);
        List<String> actualMethodNames = materializedMission.getTasksNames();
        List<String> expectedMethodNames = TestDefinitions.METHODS_LIST.stream()
                .map(method -> method.getName())
                .collect(Collectors.toList());
        assertEquals(expectedMethodNames.size(), actualMethodNames.size());
        assertTrue(expectedMethodNames.stream().allMatch(actualMethodNames::contains));
    }
}