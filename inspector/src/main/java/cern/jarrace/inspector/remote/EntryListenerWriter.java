package cern.jarrace.inspector.remote;

import cern.jarrace.inspector.entry.EntryListener;
import cern.jarrace.inspector.entry.EntryState;
import com.google.gson.Gson;

import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * An implementation of an {@link EntryListener} which relays listener calls to a {@link java.io.PrintWriter}.
 */
public class EntryListenerWriter implements EntryListener, Flushable {

    private final PrintWriter writer;
    private final Gson gson = new Gson();

    /**
     * Creates a writer which uses the given {@link PrintWriter} to forward listener calls.
     *
     * @param writer The writer to send calls to.
     */
    public EntryListenerWriter(PrintWriter writer) {
        this.writer = writer;
    }
    
    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void onLocationChange(EntryState state) {
        writer.println(gson.toJson(state));
    }

    @Override
    public void onInspectionEnd(EntryState state) {
        writer.println(gson.toJson(state));
    }

}
