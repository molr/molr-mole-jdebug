/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package io.molr.mole.jdebug.spawner.json;

import cern.molr.commons.domain.JdiMission;
import cern.molr.commons.domain.impl.MissionImpl;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A GSON serialiser and de-serialiser for {@link JdiMission} objects.
 */
public class MissionTypeAdapter extends TypeAdapter<JdiMission> {

    private static final String LIST_SEPARATOR = ",";

    @Override
    public void write(JsonWriter out, JdiMission jdiMission) throws IOException {
        Objects.requireNonNull(jdiMission.getMoleClassName(), "Agent name cannot be null");
        Objects.requireNonNull(jdiMission.getMissionContentClassName(), "Class name cannot be null");
        Objects.requireNonNull(jdiMission.getTasksNames(), "Entry points list cannot be null");
        out.beginObject()
                .name("agentName")
                .value(jdiMission.getMoleClassName())
                .name("className")
                .value(jdiMission.getMissionContentClassName())
                .name("entryPoints")
                .value(jdiMission.getTasksNames().stream().collect(Collectors.joining(LIST_SEPARATOR)))
                .endObject();
    }

    @Override
    public JdiMission read(JsonReader in) throws IOException {
        in.beginObject();
        in.nextName();
        final String agentName = in.nextString();
        in.nextName();
        final String className = in.nextString();
        in.nextName();
        final String entryPointsString = in.nextString();
        in.endObject();

        return new MissionImpl(agentName, className, Arrays.asList(entryPointsString.split(LIST_SEPARATOR)));
    }
}