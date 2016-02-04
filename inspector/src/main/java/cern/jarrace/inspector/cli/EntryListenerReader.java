package cern.jarrace.inspector.cli;

import cern.jarrace.inspector.entry.EntryListener;
import cern.jarrace.inspector.entry.EntryState;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

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

    @Override
    protected void readCommand(BufferedReader reader) {
        try {
            String input = reader.readLine();
            if (input != null) {
                EntryState state = gson.fromJson(input, EntryState.class);
                listener.onLocationChange(state);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read call to entry listener: ", e);
        }
    }
}
