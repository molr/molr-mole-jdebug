/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import cern.jarrace.inspector.jdi.EntryState;
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
    public void onLocationChange(EntryState state) {
        insertEntry(state);
    }

    @Override
    public void onInspectionEnd(EntryState state) {
        insertEntry(state);
    }

    /**
     * Attempts to serve an {@link EntryState} to any thread trying to pull events from the stateQueue. If
     * no one is listening an error will be logged, but nothing else will happen.
     *
     * @param entryState The state of an entry given from a running JDI instance.
     */
    private void insertEntry(EntryState entryState) {
        try {
            stateQueue.offer(entryState, timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to insert location change state for state {}: Unexpected location change", entryState);
        }
    }

}
