/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller;

import java.util.HashMap;
import java.util.Map;

import cern.jarrace.inspector.ThreadState;
import cern.jarrace.inspector.Inspector;
import cern.jarrace.inspector.inspectable.EntryListener;
import cern.jarrace.inspector.inspectable.CallbackFactory;
import cern.jarrace.inspector.inspectable.LocationRange;
import org.jdiscript.JDIScript;
import org.jdiscript.requests.ChainingStepRequest;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.StepRequest;

/**
 * An event handler for the {@link Inspector} main class.
 */
public class JdiEventHandlerImpl extends JdiEventHandler {

    private final JDIScript jdi;
    private final CallbackFactory<?> callbackHandler;
    private final Map<ThreadReference, InspectableState> threads = new HashMap<>();
    private final Map<String, ThreadReference> classNamesToThreads = new HashMap<>();

    public JdiEventHandlerImpl(JDIScript jdi, CallbackFactory<?> callbackHandler) {
        super(jdi.vm());
        this.jdi = jdi;
        this.callbackHandler = callbackHandler;
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

            final EntryListener callbackListener = callbackHandler.onBreakpoint(
                    event.thread(), new ThreadState(ThreadState.StepDirection.FORWARD, range, event.location()));
            final InspectableState state = new InspectableState(callbackListener, range);
            threads.put(event.thread(), state);
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
                threads.get(e.thread()).listener.onLocationChange(new ThreadState(ThreadState.StepDirection.FORWARD,
                        state.methodRange, e.location()));
                e.thread().suspend();
            } else {
                threads.remove(e.thread()).listener.onInspectionEnd();
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

        InspectableState(EntryListener listener, LocationRange methodRange) {
            this.listener = listener;
            this.methodRange = methodRange;
        }

    }

}
