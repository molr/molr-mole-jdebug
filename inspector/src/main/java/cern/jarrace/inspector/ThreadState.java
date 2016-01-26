/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector;

import cern.jarrace.inspector.jdi.LocationRange;
import com.sun.jdi.Location;

/**
 * An immutable state for a {@link com.sun.jdi.ThreadReference}.
 */
public class ThreadState {

    private final StepDirection stepDirection;
    private final LocationRange inspectableRange;
    private final Location currentLocation;

    public ThreadState(StepDirection stepDirection, LocationRange inspectableRange, Location currentLocation) {
        this.stepDirection = stepDirection;
        this.inspectableRange = inspectableRange;
        this.currentLocation = currentLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public StepDirection getStepDirection() {
        return stepDirection;
    }

    public Location getStartLocation() {
        return inspectableRange.getStart();
    }

    public Location getEndLocation() {
        return inspectableRange.getEnd();
    }

    public ThreadState setLocation(Location location) {
        return new ThreadState(stepDirection, inspectableRange, location);
    }

    public enum StepDirection {
        BACKWARD, FORWARD;
    }
}
