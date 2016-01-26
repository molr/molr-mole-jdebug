/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.entry;

import cern.jarrace.inspector.ThreadState;
import com.sun.jdi.Location;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 
 */
public class BlockingCallbackListener implements EntryListener {

    AtomicReference<Location> location = new AtomicReference<>();

    public BlockingCallbackListener() {
        /* Should only be instantiated via the static method listen() */
    }

    @Override
    public void onLocationChange(ThreadState state) {

    }

    @Override
    public void onInspectionEnd() {

    }
}
