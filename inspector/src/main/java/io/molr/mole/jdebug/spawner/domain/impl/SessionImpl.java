package io.molr.mole.jdebug.spawner.domain.impl;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.controller.StatefulJdiController;
import io.molr.mole.jdebug.spawner.domain.Session;

import java.time.ZonedDateTime;

/**
 * @see Session
 */
public class SessionImpl implements Session {

    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final JdiMission jdiMission;
    private final StatefulJdiController controller;

    public SessionImpl(JdiMission jdiMission, StatefulJdiController controller) {
        this.jdiMission = jdiMission;
        this.controller = controller;
    }

    @Override
    public JdiMission getJdiMission() {
        return jdiMission;
    }

    @Override
    public StatefulJdiController getController() {
        return controller;
    }

    @Override
    public ZonedDateTime getTimeStamp() {
        return timestamp;
    }
}
