/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.json;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.domain.impl.MissionImpl;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A GSON serialiser and de-serialiser for {@link Mission} objects.
 */
public class MissionTypeAdapter extends TypeAdapter<Mission> {

    private static final String LIST_SEPARATOR = ",";

    @Override
    public void write(JsonWriter out, Mission mission) throws IOException {
        Objects.requireNonNull(mission.getMoleClassName(), "Agent name cannot be null");
        Objects.requireNonNull(mission.getMissionContentClassName(), "Class name cannot be null");
        Objects.requireNonNull(mission.getTasksNames(), "Entry points list cannot be null");
        out.beginObject()
                .name("agentName")
                .value(mission.getMoleClassName())
                .name("className")
                .value(mission.getMissionContentClassName())
                .name("entryPoints")
                .value(mission.getTasksNames().stream().collect(Collectors.joining(LIST_SEPARATOR)))
                .endObject();
    }

    @Override
    public Mission read(JsonReader in) throws IOException {
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