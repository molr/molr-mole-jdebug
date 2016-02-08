package cern.jarrace.commons.gson;

import cern.jarrace.commons.domain.Service;
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

public class ServiceTypeAdapterTest {

    private static final List<String> ENTRY_POINTS = Arrays.asList("one", "two");
    private static final Service SERVICE = new Service("testAgent", "testClass", ENTRY_POINTS);
    private static final ServiceTypeAdapter TYPE_ADAPTER = new ServiceTypeAdapter();

    @Test
    public void serialisesAService() throws IOException {
        final String json = "{\"agentName\":\"testAgent\",\"className\":\"testClass\",\"entryPoints\":\"one,two\"}";
        assertEquals(json, write(SERVICE));
    }

    @Test(expected = NullPointerException.class)
    public void failsToSerialiseServiceWithNullAgent() throws IOException {
        write(new Service(null, "notNull", ENTRY_POINTS));
    }

    @Test(expected = NullPointerException.class)
    public void failsToSerialiseServiceWithNullClass() throws IOException {
        write(new Service("notNull", null, ENTRY_POINTS));
    }

    @Test(expected = NullPointerException.class)
    public void failsToSerialiseServiceWithNullEntries() throws IOException {
        write(new Service("notNull", "notNull", null));
    }

    @Test
    public void deserialisesAService() throws IOException {
        final String json = "{\"agentName\":\"testAgent\",\"className\":\"testClass\",\"entryPoints\":\"one,two\"}";
        assertEquals(SERVICE, read(json));
    }

    @Test(expected = MalformedJsonException.class)
    public void failsToDeserialiseOnMalformedJson() throws IOException {
        final String json = "{\"agentName\":malformed";
        read(json);
    }

    private Service read(String json) throws IOException {
        return TYPE_ADAPTER.read(new JsonReader(new StringReader(json)));
    }

    private String write(Service service) throws IOException {
        final StringWriter writer = new StringWriter();
        TYPE_ADAPTER.write(new JsonWriter(writer), service);
        return writer.toString();
    }

}