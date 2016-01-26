/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import cern.jarrace.inspector.ThreadState;
import cern.jarrace.inspector.controller.JdiEntryRegistry;
import com.sun.jdi.ThreadReference;

/**
 * A {@link CallbackFactory} which
 */
public class BlockingCallbackFactory implements CallbackFactory<BlockingEntryListener> {

    private final JdiEntryRegistry entryRegistry;

    public BlockingCallbackFactory(JdiEntryRegistry entryRegistry) {
        this.entryRegistry = entryRegistry;
    }

    @Override
    public BlockingEntryListener onBreakpoint(ThreadReference thread, ThreadState state) {
        return null;
    }

}
