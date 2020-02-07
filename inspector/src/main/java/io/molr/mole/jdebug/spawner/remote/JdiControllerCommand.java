package io.molr.mole.jdebug.spawner.remote;

import io.molr.mole.jdebug.spawner.controller.JdiController;

/**
 * Commands which can be issued to the {@link JdiController} remotely.
 */
public enum JdiControllerCommand {
    STEP_FORWARD, TERMINATE, RESUME
}
