/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry.impl;

import cern.jarrace.inspector.entry.EntryState;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * An immutable implementation of an {@link EntryState}.
 */
public class EntryStateImpl implements EntryState {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryStateImpl.class);

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

    public static Optional<EntryState> ofLocation(Location location) {
        try {
            final String className = location.sourceName();
            final String methodName = location.method().name();
            final EntryState entryState = new EntryStateImpl(className, methodName, location.lineNumber());
            return Optional.of(entryState);
        } catch (AbsentInformationException e) {
            LOGGER.warn("Failed to get entry state from thread state: missing source name of thread class", e);
            return Optional.empty();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntryStateImpl that = (EntryStateImpl) o;

        if (position != that.position) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        return methodName != null ? methodName.equals(that.methodName) : that.methodName == null;

    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + position;
        return result;
    }

}
