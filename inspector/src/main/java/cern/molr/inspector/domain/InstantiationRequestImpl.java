/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.domain;

import cern.molr.commons.domain.Service;

/**
 * An immutable implementation of an {@link InstantiationRequest}.
 */
public class InstantiationRequestImpl implements InstantiationRequest {

    private final String classPath;
    private final Service service;

    /**
     * Creates a {@link InstantiationRequestImpl} using the given class path and {@link Service}.
     *
     * @param classPath The class path containing zero or more paths separated by the {@link java.io.File#pathSeparator}.
     * @param service   The service to execute.
     */
    public InstantiationRequestImpl(String classPath, Service service) {
        this.classPath = classPath;
        this.service = service;
    }

    @Override
    public String getClassPath() {
        return classPath;
    }

    @Override
    public Service getService() {
        return service;
    }

}