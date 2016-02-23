package cern.molr.inspector.domain;

import cern.molr.commons.domain.Mission;
import cern.molr.inspector.controller.JdiController;

import java.time.ZonedDateTime;

/**
 * A generic {@link Session}, encapsulates the information of a currently running {@link Mission}
 *
 * @author tiagomr
 */
public interface Session {

    /**
     * @return the {@link Mission} being executed
     */
    Mission getMission();

    /**
     * @return the {@link JdiController} used to control the execution flow for this specific execution
     */
    JdiController getController();

    /**
     * @return a {@link ZonedDateTime} to timestamp the creation of the {@link Session}
     */
    ZonedDateTime getTimeStamp();
}
