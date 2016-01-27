/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import cern.jarrace.inspector.jdi.EntryState;
import cern.jarrace.inspector.jdi.ThreadState;
import cern.jarrace.inspector.jdi.impl.EntryStateImpl;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class BlockingEntryListener implements EntryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingEntryListener.class);

    private final SynchronousQueue<EntryState> stateQueue = new SynchronousQueue<>();
    private final Duration timeout;

    public BlockingEntryListener(Duration timeout) {
        this.timeout = timeout;
    }

    public EntryState waitForNextEntry() throws InterruptedException {
        return stateQueue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void onLocationChange(ThreadState state) {
        insertEntry(state);
    }

    @Override
    public void onInspectionEnd(ThreadState state) {
        insertEntry(state);
    }

    /**
     * Attempts to serve an {@link EntryState} to any thread trying to pull events from the stateQueue. If
     * no one is listening an error will be logged, but nothing else will happen.
     *
     * @param threadState The state of a thread given from a running JDI instance.
     */
    private void insertEntry(ThreadState threadState) {
        try {
            final Location currentLocation = threadState.getCurrentLocation();
            final String className = currentLocation.sourceName();
            final String methodName = currentLocation.method().name();
            final EntryState entryState = new EntryStateImpl(className, methodName, currentLocation.lineNumber());
            stateQueue.offer(entryState, timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to insert location change state for state {}: Unexpected location change", threadState);
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }

}
