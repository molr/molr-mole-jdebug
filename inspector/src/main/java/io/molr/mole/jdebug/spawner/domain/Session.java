package io.molr.mole.jdebug.spawner.domain;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.controller.StatefulJdiController;

import java.time.ZonedDateTime;

/**
 * A generic {@link Session}, encapsulates the information of a currently running {@link JdiMission}
 *
 * @author tiagomr
 */
public interface Session {

    /**
     * @return the {@link JdiMission} being executed
     */
    JdiMission getJdiMission();

    /**
     * @return the {@link StatefulJdiController} used to control the execution flow for this specific execution
     */
    StatefulJdiController getController();

    /**
     * @return a {@link ZonedDateTime} to timestamp the creation of the {@link Session}
     */
    ZonedDateTime getTimeStamp();
}
