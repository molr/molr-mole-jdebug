/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.agent.impl;

import cern.molr.agent.Agent;
import cern.molr.agent.annotations.RunWithAgent;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link Agent} which allows for the discovery and execution of Junit tests.
 * <h3>Discovery:</h3> All methods annotated with {@link Test} are be exposed as entry points and their respective
 * classes are exposed as services.
 * <h3>Execution:</h3> Allows for the execution of the whole test suite annotated with the
 * {@link RunWithAgent} annotation or individual entry points. Uses the JUnit framework for the
 * execution of the tests.
 *
 * @author tiagomr
 */
public class JunitAgent implements Agent {

    @Override
    public void initialize() {
        // Nothing to do here
    }

    @Override
    public List<Method> discover(Class<?> clazz) {
        List<Method> annotatedMethods = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    @Override
    public void run(Object... args) throws IOException {
        String entry = (String) args[0];
        try {
            Class<?> c = Class.forName(entry);
            JUnitCore.runClasses(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}