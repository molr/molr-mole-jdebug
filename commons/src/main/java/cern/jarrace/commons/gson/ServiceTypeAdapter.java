package cern.jarrace.commons.gson;

import cern.jarrace.commons.domain.Service;
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
        Objects.requireNonNull(service.getAgentName(), "Agent name cannot be null");
        Objects.requireNonNull(service.getClassName(), "Class name cannot be null");
        Objects.requireNonNull(service.getEntryPoints(), "Entry points list cannot be null");
        out.beginObject()
                .name("agentName")
                .value(service.getAgentName())
                .name("className")
                .value(service.getClassName())
                .name("entryPoints")
                .value(service.getEntryPoints().stream().collect(Collectors.joining(LIST_SEPARATOR)))
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

        return new Service(agentName, className, Arrays.asList(entryPointsString.split(LIST_SEPARATOR)));
    }
}
