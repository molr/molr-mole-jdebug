/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import cern.jarrace.inspector.jdi.EntryState;
import cern.jarrace.inspector.jdi.ThreadState;

import java.time.Duration;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class BlockingEntryListener implements EntryListener {

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

    }

    @Override
    public void onInspectionEnd() {

    }
}
