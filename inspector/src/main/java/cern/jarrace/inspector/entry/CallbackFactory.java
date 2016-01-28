/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import com.sun.jdi.ThreadReference;

/**
 * A listener for new entry instances. The method {@link #onBreakpoint(ThreadReference, EntryState)} is called
 * every time a new instance has been created and reached the first breakpoint defined in the class. This factory
 * produces {@link EntryListener}s which will receive callback events from the running JDI whenever
 * a new line is stepped over ({@link EntryListener#onLocationChange(EntryState)}) or whenever this
 * instance is killed ({@link EntryListener#onInspectionEnd(EntryState)}).
 *
 * @param <Listener> The type of EntryListener to return.
 * @author jepeders
 */
@FunctionalInterface
public interface CallbackFactory<Listener extends EntryListener> {

    /**
     * This method is called whenever a registered entry has been reached in the running JVM. The {@link EntryListener}
     * returned from this method will receive future callbacks whenever the method is being stepped through and when
     * the execution terminates.
     *
     * @param thread The reference to the thread running the method.
     * @param state  The current state of the thread running the method.
     * @return An {@link EntryListener} instance that will receive future updates whenever the JDI steps
     * through the method or terminates the execution of the method.
     */
    Listener onBreakpoint(ThreadReference thread, EntryState state);

}
