/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr;

import cern.molr.commons.exception.MissionExecutionException;
import cern.molr.commons.mole.Mole;
import cern.molr.commons.mole.RunWithMole;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Test definitions common to several test suites
 *
 * @author tiagomr
 */
public class TestDefinitions {

    public static final List<Method> METHODS_LIST = Collections.EMPTY_LIST;
    public static final int NUMBER_OF_TEST_MISSION_DEFINITIONS = 3;

    public static class TestMoleWithNoEmptyConstructor implements Mole {

        public TestMoleWithNoEmptyConstructor(int sampleArgument) {
        }

        @Override
        public List<Method> discover(Class<?> clazz) {
            return null;
        }

        @Override
        public void run(String missionContentClassName, Object... args) throws MissionExecutionException {
            throw new MissionExecutionException(new OperationNotSupportedException());
        }
    }

    public static class TestMole implements Mole {

        public TestMole() {
        }

        @Override
        public List<Method> discover(Class<?> clazz) {
            return METHODS_LIST;
        }

        @Override
        public void run(String missionContentClassName, Object... args) throws MissionExecutionException {
            throw new MissionExecutionException(new OperationNotSupportedException());
        }
    }

    @RunWithMole(Mole.class)
    public static class TestMission1 {
    }

    @RunWithMole(TestMoleWithNoEmptyConstructor.class)
    public static class TestMission2 {
    }

    @RunWithMole(TestMole.class)
    public static class TestMission3 {
    }
}
