/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import cern.jarrace.inspector.controller.JdiEntryRegistry;
import com.sun.jdi.ThreadReference;

import java.time.Duration;

/**
 * A {@link CallbackFactory} which
 */
public class BlockingCallbackFactory implements CallbackFactory<BlockingEntryListener> {

    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(2);

    private final JdiEntryRegistry<BlockingEntryListener> entryRegistry;

    public BlockingCallbackFactory(JdiEntryRegistry<BlockingEntryListener> entryRegistry) {
        this.entryRegistry = entryRegistry;
    }

    @Override
    public BlockingEntryListener onBreakpoint(ThreadReference thread, EntryState state) {
        final BlockingEntryListener listener = new BlockingEntryListener(TIMEOUT_DURATION);
        try {
            entryRegistry.register(thread, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listener;
    }

}
