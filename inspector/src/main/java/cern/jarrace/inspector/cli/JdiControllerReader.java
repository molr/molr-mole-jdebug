package cern.jarrace.inspector.cli;

import cern.jarrace.inspector.controller.JdiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reads commands from a {@link java.io.BufferedReader} and proxies them to a given
 * {@link cern.jarrace.inspector.controller.JdiController}. The reader runs a separate thread pool to continuously read
 * input from the stream.
 */
public class JdiControllerReader extends RemoteReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdiControllerReader.class);

    private final JdiController controller;

    /**
     * Creates a reader which reads commands from the given reader and forwards them to the controller.
     *
     * @param reader     The reader to read incoming commands from.
     * @param controller The controller to relay commands to.
     */
    public JdiControllerReader(BufferedReader reader, JdiController controller) {
        super(reader);
        this.controller = controller;
    }

    private static void closeResource(AutoCloseable closeable, String error) {
        try {
            closeable.close();
        } catch (Exception e) {
            LOGGER.warn(error, e);
        }
    }

    private void forwardCommand(JdiControllerCommand command) {
        switch (command) {
            case STEP_FORWARD:
                controller.stepForward();
                break;
            case TERMINATE:
                controller.terminate();
                break;
        }
    }

    protected void readCommand(BufferedReader reader) {
        try {
            int code = reader.read();
            JdiControllerCommand[] values = JdiControllerCommand.values();
            if (code > 0 && code < values.length) {
                forwardCommand(values[code]);
            } else {
                LOGGER.error("Received illegal command {}, expected a number between 0 and {}", code, values.length - 1);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to read command from reader: ", e);
        }
    }

}
