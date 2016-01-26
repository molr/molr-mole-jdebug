/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.inspectable;

import cern.jarrace.inspector.ThreadState;
import com.sun.jdi.ThreadReference;

/**
 * A listener for new inspectable instances. The method {@link #onBreakpoint(ThreadReference, ThreadState)} is called
 * every time a new instance has been created and reached the first breakpoint defined in the class. This factory
 * produces {@link EntryListener}s which will receive callback events from the running JDI whenever
 * a new line is stepped over ({@link EntryListener#onLocationChange(ThreadState)}) or whenever this
 * instance is killed ({@link EntryListener#onInspectionEnd()}).
 *
 * @param <Listener> The type of EntryListener to return.
 * @author jepeders
 */
@FunctionalInterface
public interface CallbackFactory<Listener extends EntryListener> {

    /**
     * This method is called whenever a registered breakpoint has been reached in the running JVM. In effect this means
     * that the method containing the breakpoint is ready for execution. The {@link EntryListener}
     * returned from this method will receive future callbacks whenever the method is being stepped through and when
     * the execution terminates.
     *
     * @param thread The reference to the thread running the method.
     * @param state  The current state of the thread running the method.
     * @return An {@link EntryListener} instance that will receive future updates whenever the JDI steps
     * through the method or terminates the execution of the method.
     */
    Listener onBreakpoint(ThreadReference thread, ThreadState state);

}
