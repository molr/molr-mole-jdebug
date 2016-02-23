package cern.molr.inspector.domain.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.domain.Session;

import java.time.ZonedDateTime;

/**
 * @see Session
 */
public class SessionImpl implements Session {

    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final Mission mission;
    private final JdiController controller;

    public SessionImpl(Mission mission, JdiController controller) {
        this.mission = mission;
        this.controller = controller;
    }

    @Override
    public Mission getMission() {
        return mission;
    }

    @Override
    public JdiController getController() {
        return controller;
    }

    @Override
    public ZonedDateTime getTimeStamp() {
        return timestamp;
    }
}
