/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller;

import cern.jarrace.inspector.entry.EntryListener;
import cern.jarrace.inspector.entry.EntryListenerFactory;
import cern.jarrace.inspector.entry.impl.EntryStateImpl;
import cern.jarrace.inspector.jdi.LocationRange;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.StepRequest;
import org.jdiscript.JDIScript;
import org.jdiscript.requests.ChainingStepRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * An event handler receiving events from the running JDI instance. This handler attempts to hide
 * some of the JDI implementations, so it should not be used outside the {@link JdiController}.
 */
public class SteppingJdiEventHandler extends JdiEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SteppingJdiEventHandler.class);

    private final JDIScript jdi;
    private final Map<ThreadReference, InspectableState> threads = new HashMap<>();
    private final Map<String, ThreadReference> classNamesToThreads = new HashMap<>();
    private final EntryListenerFactory<?> callbackHandler;

    /**
     * Creates a new event handler that is
     *
     * @param jdi
     * @param callbackFactory
     */
    public SteppingJdiEventHandler(JDIScript jdi, EntryListenerFactory<?> callbackFactory) {
        super(jdi.vm());
        this.jdi = jdi;
        this.callbackHandler = callbackFactory;
    }

    @Override
    public void breakpoint(BreakpointEvent event) {
        try {
            final ThreadReference threadReference = event.thread();
            threadReference.suspend();
            final LocationRange range = LocationRange.ofMethod(event.location().method());
            final ChainingStepRequest request = jdi.stepRequest(threadReference, StepRequest.STEP_LINE,
                    StepRequest.STEP_OVER);
            request.addHandler(this);
            request.enable();

            final String sourcePath = event.location().sourcePath();
            classNamesToThreads.put(sourcePath, event.thread());

            EntryStateImpl.ofLocation(event.location()).ifPresent(entryState -> {
                final EntryListener callbackListener = callbackHandler.createListenerOn(
                        event.thread(), entryState);
                final InspectableState state = new InspectableState(callbackListener, range);
                threads.put(event.thread(), state);
            });
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }

    public ThreadReference getReferenceForClass(String className) {
        return classNamesToThreads.get(className);
    }

    @Override
    public synchronized void step(StepEvent e) {
        InspectableState state = threads.get(e.thread());
        if (state != null) {
            if (state.methodRange.isWithin(e.location())) {
                EntryStateImpl.ofLocation(e.location()).ifPresent(entryState ->
                        threads.get(e.thread()).listener.onLocationChange(entryState));
                e.thread().suspend();
            } else {
                EntryStateImpl.ofLocation(e.location()).ifPresent(entryState ->
                        threads.remove(e.thread()).listener.onInspectionEnd(entryState));
            }
        }
    }

    @Override
    public void vmStart(VMStartEvent e) {
        // Do nothing
    }

    private static class InspectableState {

        private final EntryListener listener;
        private final LocationRange methodRange;

        private InspectableState(EntryListener listener, LocationRange methodRange) {
            this.listener = listener;
            this.methodRange = methodRange;
        }

    }

}
