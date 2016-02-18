/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.json;

import cern.molr.commons.domain.Service;
import cern.molr.commons.domain.impl.ServiceImpl;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A GSON serialiser and de-serialiser for {@link Service} objects.
 */
public class ServiceTypeAdapter extends TypeAdapter<Service> {

    private static final String LIST_SEPARATOR = ",";

    @Override
    public void write(JsonWriter out, Service service) throws IOException {
        Objects.requireNonNull(service.getMoleClassName(), "Agent name cannot be null");
        Objects.requireNonNull(service.getServiceClassName(), "Class name cannot be null");
        Objects.requireNonNull(service.getTasksNames(), "Entry points list cannot be null");
        out.beginObject()
                .name("agentName")
                .value(service.getMoleClassName())
                .name("className")
                .value(service.getServiceClassName())
                .name("entryPoints")
                .value(service.getTasksNames().stream().collect(Collectors.joining(LIST_SEPARATOR)))
                .endObject();
    }

    @Override
    public Service read(JsonReader in) throws IOException {
        in.beginObject();
        in.nextName();
        final String agentName = in.nextString();
        in.nextName();
        final String className = in.nextString();
        in.nextName();
        final String entryPointsString = in.nextString();
        in.endObject();

        return new ServiceImpl(agentName, className, Arrays.asList(entryPointsString.split(LIST_SEPARATOR)));
    }
}