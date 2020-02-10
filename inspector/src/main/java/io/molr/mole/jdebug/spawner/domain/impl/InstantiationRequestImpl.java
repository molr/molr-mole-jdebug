/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package io.molr.mole.jdebug.spawner.domain.impl;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.domain.InstantiationRequest;

/**
 * An immutable implementation of an {@link InstantiationRequest}.
 */
public class InstantiationRequestImpl implements InstantiationRequest {

    private final String classPath;
    private final JdiMission jdiMission;

    /**
     * Creates a {@link InstantiationRequestImpl} using the given class path and {@link JdiMission}.
     *
     * @param classPath The class path containing zero or more paths separated by the {@link java.io.File#pathSeparator}.
     * @param jdiMission   The mission to execute.
     */
    public InstantiationRequestImpl(String classPath, JdiMission jdiMission) {
        this.classPath = classPath;
        this.jdiMission = jdiMission;
    }

    @Override
    public String getClassPath() {
        return classPath;
    }

    public JdiMission getJdiMission() {
        return jdiMission;
    }

}