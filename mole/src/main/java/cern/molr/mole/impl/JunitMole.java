/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.mole.impl;

import cern.molr.commons.exception.MissionExecutionException;
import cern.molr.commons.mole.Mole;
import cern.molr.commons.mole.RunWithMole;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link Mole} which allows for the discovery and execution of Junit tests.
 * <h3>Discovery:</h3> All methods annotated with {@link Test} are be exposed as entry points and their respective
 * classes are exposed as services.
 * <h3>Execution:</h3> Allows for the execution of the whole test suite annotated with the
 * {@link RunWithMole} annotation or individual entry points. Uses the JUnit framework for the
 * execution of the tests.
 *
 * @author tiagomr
 * @see Mole
 */
public class JunitMole implements Mole {

    @Override
    public List<Method> discover(Class<?> classType) {
        if (null == classType) {
            throw new IllegalArgumentException("Class type cannot be null");
        }
        List<Method> annotatedMethods = new ArrayList<>();
        Method[] methods = classType.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    @Override
    public void run(String missionContentClassName, Object... args) throws MissionExecutionException {
        if (null == missionContentClassName) {
            throw new MissionExecutionException(new IllegalArgumentException("Mission content class name cannot be null"));
        }
        try {
            Class<?> c = Class.forName(missionContentClassName);
            JUnitCore.runClasses(c);
        } catch (Exception exception) {
            throw new MissionExecutionException(exception);
        }
    }
}