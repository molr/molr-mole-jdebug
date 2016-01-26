/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import cern.jarrace.inspector.ThreadState;
import cern.accsoft.lhc.inspector.controller.JdiMethodRegistry;
import com.sun.jdi.ThreadReference;

/**
 * A {@link CallbackFactory} which
 */
public class BlockingCallbackFactory implements CallbackFactory<BlockingCallbackListener> {

    private final JdiMethodRegistry classRegistry;

    public BlockingCallbackFactory(JdiMethodRegistry classRegistry) {
        this.classRegistry = classRegistry;
    }

    @Override
    public BlockingCallbackListener onBreakpoint(ThreadReference thread, ThreadState state) {
        return null;
    }

}
