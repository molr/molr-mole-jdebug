/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.inspector.jdi.ThreadState;
import cern.jarrace.inspector.entry.EntryListener;

/**
 * An implementation of the {@link EntryListener} which handles event callbacks for the {@link Stepper}
 * gui.
 */
public class StepperCallbackHandler implements EntryListener {

    private StepperTab tab;

    public StepperCallbackHandler(StepperTab tab) {
        this.tab = tab;
    }

    @Override
    public void onLocationChange(ThreadState state) {
        // TODO: Remove dependency on jdi
//        tab.highlight(state.getCurrentLocation().lineNumber());
    }

    @Override
    public void onInspectionEnd() {
        tab.close();
    }

}
