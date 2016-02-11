package cern.jarrace.inspector.remote;

import cern.jarrace.inspector.entry.EntryListener;
import cern.jarrace.inspector.entry.EntryState;
import cern.jarrace.inspector.entry.impl.EntryStateImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

/**
 * A reader which continually listens for commands from a {@link java.io.BufferedReader}.
 */
public class EntryListenerReader extends RemoteReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryListenerReader.class);

    private final Gson gson = new Gson();
    private final EntryListener listener;

    /**
     * Instructs the reader to receive commands from the given {@link BufferedReader} and forward them to a
     * {@link EntryListener}.
     *
     * @param reader   The reader to read commands from.
     * @param listener The listener that should receive the commands.
     */
    public EntryListenerReader(BufferedReader reader, EntryListener listener) {
        super(reader);
        this.listener = listener;
    }

    /**
     * Instructs the reader to receive commands from the given {@link BufferedReader} and forward them to a
     * {@link EntryListener}.
     *
     * @param reader       The reader to read commands from.
     * @param listener     The listener that should receive the commands.
     * @param readInterval The time interval between checks for new input.
     */
    public EntryListenerReader(BufferedReader reader, EntryListener listener, Duration readInterval) {
        super(reader, readInterval);
        this.listener = listener;
    }

    private void forwardEntry(EntryListenerMethod method, EntryState state) {
        switch (method) {
            case ON_LOCATION_CHANGE:
                listener.onLocationChange(state);
                break;
            case ON_INSPECTION_END:
                listener.onInspectionEnd(state);
                break;
        }
    }

    @Override
    protected void readCommand(BufferedReader reader) {
        try {
            Optional<EntryListenerMethod> listenerMethod = readCommand(reader.readLine());
            Optional<EntryState> listenerState = readState(reader.readLine());
            if (listenerMethod.isPresent() && listenerMethod.isPresent()) {
                forwardEntry(listenerMethod.get(), listenerState.get());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read call to entry listener: ", e);
        }
    }

    private Optional<EntryListenerMethod> readCommand(String input) {
        if (input != null) {
            if (input.length() != 1) {
                LOGGER.warn("Expected numeric character, but received {}", input);
            } else {
                int method = Character.getNumericValue(input.charAt(0));
                EntryListenerMethod[] methods = EntryListenerMethod.values();
                if (method != -1) {
                    if (method >= 0 && method < methods.length) {
                        return Optional.of(methods[method]);
                    } else {
                        LOGGER.warn("Received illegal command {}, expected a number between 0 and {}", method, methods.length);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Optional<EntryState> readState(String input) {
        if (input != null) {
            if (input.isEmpty()) {
                LOGGER.warn("Expected an EntryState, but got {}", input);
            } else {
                try {
                    return Optional.of(gson.fromJson(input, EntryStateImpl.class));
                } catch (JsonSyntaxException e) {
                    LOGGER.warn("Error when parsing json", e);
                }
            }
        }

        return Optional.empty();
    }
}
