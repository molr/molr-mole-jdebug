package cern.molr.inspector.domain.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.inspector.controller.StatefulJdiController;
import cern.molr.inspector.domain.Session;

import java.time.ZonedDateTime;

/**
 * @see Session
 */
public class SessionImpl implements Session {

    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final Mission mission;
    private final StatefulJdiController controller;

    public SessionImpl(Mission mission, StatefulJdiController controller) {
        this.mission = mission;
        this.controller = controller;
    }

    @Override
    public Mission getMission() {
        return mission;
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
