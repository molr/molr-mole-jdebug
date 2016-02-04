package cern.jarrace.inspector.cli;

/**
 * Commands which can be issued to the {@link cern.jarrace.inspector.controller.JdiController} remotely.
 */
public enum JdiControllerCommand {
    STEP_FORWARD, TERMINATE;
}
