package cern.jarrace.inspector.cli;

import cern.jarrace.inspector.controller.JdiController;

import java.io.PrintWriter;

/**
 * A controller which is connected to a remote implementation of a {@link JdiController} via a given output stream.
 */
public class JdiControllerWriter implements JdiController {

    private final PrintWriter printWriter;

    /**
     * Use the given {@link PrintWriter} to write commands to a remote {@link JdiController}.
     *
     * @param printWriter A writer connected to a controller.
     */
    public JdiControllerWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    @Override
    public void stepForward() {
        printWriter.print(JdiControllerCommand.STEP_FORWARD.ordinal());
    }

    @Override
    public void terminate() {
        printWriter.print(JdiControllerCommand.TERMINATE.ordinal());
    }

}
