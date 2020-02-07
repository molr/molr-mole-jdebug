/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package io.molr.mole.jdebug.spawner.json;

import cern.molr.commons.domain.JdiMission;
import cern.molr.commons.domain.impl.MissionImpl;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MissionTypeAdapterTest {

    private static final List<String> ENTRY_POINTS = Arrays.asList("one", "two");
    private static final JdiMission SERVICE_IMPL = new MissionImpl("testAgent", "testClass", ENTRY_POINTS);
    private static final MissionTypeAdapter TYPE_ADAPTER = new MissionTypeAdapter();

    @Test
    public void serialisesAService() throws IOException {
        final String json = "{\"agentName\":\"testAgent\",\"className\":\"testClass\",\"entryPoints\":\"one,two\"}";
        assertEquals(json, write(SERVICE_IMPL));
    }

    @Test(expected = NullPointerException.class)
    public void failsToSerialiseServiceWithNullAgent() throws IOException {
        write(new MissionImpl(null, "notNull", ENTRY_POINTS));
    }

    @Test(expected = NullPointerException.class)
    public void failsToSerialiseServiceWithNullClass() throws IOException {
        write(new MissionImpl("notNull", null, ENTRY_POINTS));
    }

    @Test(expected = NullPointerException.class)
    public void failsToSerialiseServiceWithNullEntries() throws IOException {
        write(new MissionImpl("notNull", "notNull", null));
    }

    @Test
    public void deserializesAService() throws IOException {
        final String json = "{\"agentName\":\"testAgent\",\"className\":\"testClass\",\"entryPoints\":\"one,two\"}";
        assertEquals(SERVICE_IMPL, read(json));
    }

    @Test(expected = MalformedJsonException.class)
    public void failsToDeserialiseOnMalformedJson() throws IOException {
        final String json = "{\"agentName\":malformed";
        read(json);
    }

    private JdiMission read(String json) throws IOException {
        return TYPE_ADAPTER.read(new JsonReader(new StringReader(json)));
    }

    private String write(JdiMission service) throws IOException {
        final StringWriter writer = new StringWriter();
        TYPE_ADAPTER.write(new JsonWriter(writer), service);
        return writer.toString();
    }

}