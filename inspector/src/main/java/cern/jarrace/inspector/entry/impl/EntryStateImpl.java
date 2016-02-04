/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry.impl;

import cern.jarrace.inspector.entry.EntryState;

/**
 * An immutable implementation of an {@link EntryState}.
 */
public class EntryStateImpl implements EntryState {

    private final String className;
    private final String methodName;
    private final int position;

    /**
     * Creates an immutable entry state with the given constants.
     *
     * @param className  The name of the class containing the entry.
     * @param methodName The name of the method for the entry.
     * @param position   The position of the current execution inside the entry.
     */
    public EntryStateImpl(String className, String methodName, int position) {
        this.className = className;
        this.methodName = methodName;
        this.position = position;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public int getLine() {
        return position;
    }



}
