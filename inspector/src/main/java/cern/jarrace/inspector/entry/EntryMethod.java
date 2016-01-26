/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import java.lang.reflect.Method;

/**
 * An immutable representation of an {@link Method} that can act as an entry-point (see
 * {@link cern.jarrace.inspector.EntryState}). Currently the method name must not appear more than once
 * (cannot be overloaded) because JDI has a hard time distinguishing between overloaded methods.
 *
 * @author jepeders
 */
public class EntryMethod {

    private final Class<?> inspectableClass;
    private final String methodName;

    private EntryMethod(Class<?> inspectableClass, String methodName) {
        this.inspectableClass = inspectableClass;
        this.methodName = methodName;
    }

    public Class<?> getMethodClass() {
        return inspectableClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public static EntryMethod ofClassAndMethod(Class<?> inspectableClass, String methodName)
            throws SecurityException, NoSuchMethodException {
        verifyNotOverloaded(inspectableClass, methodName);
        return new EntryMethod(inspectableClass, methodName);
    }

    private static void verifyNotOverloaded(Class<?> classWithMethod, String methodName) throws NoSuchMethodException {
        int methodCount = 0;
        for (Method method : classWithMethod.getMethods()) {
            if (method.getName().equals(methodName)) {
                methodCount++;
            }
        }
        if (methodCount > 1) {
            throw new IllegalArgumentException("Method " + methodName + " cannot be overloaded. Found " + methodCount
                    + " instances.");
        } else if (methodCount < 1) {
            throw new NoSuchMethodException(methodName);
        }
    }
}
