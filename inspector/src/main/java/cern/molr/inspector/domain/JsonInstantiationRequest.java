/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.domain;

import cern.jarrace.commons.domain.Service;
import cern.molr.inspector.json.ServiceTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * An immutable JSON implementation of an {@link InstantiationRequest}.
 */
public class JsonInstantiationRequest implements InstantiationRequest {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Service.class, new ServiceTypeAdapter().nullSafe())
            .create();
    private final String classPath;
    private final Service service;

    /**
     * Creates a {@link JsonInstantiationRequest} using the given class path and {@link Service}.
     *
     * @param classPath The class path containing zero or more paths separated by the {@link java.io.File#pathSeparator}.
     * @param service   The service to execute.
     */
    public JsonInstantiationRequest(String classPath, Service service) {
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

    /**
     * @return A JSON formatted string, representing the data in <code>this</code> object.
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * Attempts to deserialise the input {@link String} to a {@link JsonInstantiationRequest}.
     *
     * @param input A JSON formatted string.
     * @return An instance of a {@link JsonInstantiationRequest}.
     * @throws JsonSyntaxException If the string was not well-formed JSON.
     */
    public static JsonInstantiationRequest fromJson(String input) throws JsonSyntaxException {
        return GSON.fromJson(input, JsonInstantiationRequest.class);
    }

}