package cern.jarrace.commons.gson;

import cern.jarrace.commons.domain.Service;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * A GSON serialiser and de-serialiser for {@link Service} objects.
 */
public class ServiceTypeAdapter extends TypeAdapter<Service> {

    private final String LIST_SEPARATOR = ",";

    @Override
    public void write(JsonWriter out, Service service) throws IOException {
        out.beginObject()
                .name("agentName")
                .value(service.getAgentName())
                .name("className")
                .value(service.getClassName())
                .name("entryPoints")
                .beginArray()
                .value(service.getEntryPoints().toString())
                .endArray()
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
        in.beginArray();
        final String entryPointsString = in.nextString();
        in.endArray();
        in.endObject();

        return new Service(agentName, className, Arrays.asList(entryPointsString.split(LIST_SEPARATOR)));
    }
}
