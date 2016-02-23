/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.controller;

import cern.molr.inspector.entry.EntryListener;
import cern.molr.inspector.entry.EntryListenerFactory;
import cern.molr.inspector.entry.EntryStateBuilder;
import cern.molr.inspector.jdi.LocationRange;
import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.StepRequest;
import org.jdiscript.JDIScript;
import org.jdiscript.requests.ChainingStepRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An event handler receiving events from the running JDI instance. This handler attempts to hide
 * some of the JDI implementations, so it should not be used outside the {@link JdiController}.
 */
public class SteppingJdiEventHandler extends JdiEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SteppingJdiEventHandler.class);

    private final JDIScript jdi;
    private final EntryListenerFactory<?> callbackHandler;
    private final JdiEntryRegistry<EntryListener> registry;

    /**
     * Creates a new event handler that is
     *
     * @param jdi             The {@link JDIScript} instance that interfaces Java Debug Interface (JDI).
     * @param callbackFactory A factory that can create new {@link EntryListener}s when a new instance that should be
     *                        listened to is spawned in the JVM.
     */
    public SteppingJdiEventHandler(JDIScript jdi, EntryListenerFactory<?> callbackFactory, JdiEntryRegistry<EntryListener> registry) {
        super(jdi.vm());
        this.jdi = jdi;
        this.callbackHandler = callbackFactory;
        this.registry = registry;
    }

    @Override
    public void breakpoint(BreakpointEvent event) {
        final ThreadReference threadReference = event.thread();
        threadReference.suspend();
        final ChainingStepRequest request = jdi.stepRequest(threadReference, StepRequest.STEP_LINE,
                StepRequest.STEP_OVER);
        request.addHandler(this);
        request.enable();

        EntryStateBuilder.ofLocation(event.location()).ifPresent(entryState -> {
            final EntryListener callbackListener = callbackHandler.createListenerOn(
                    event.thread(), entryState);
            registry.register(event.thread(), callbackListener);
            callbackListener.onLocationChange(entryState);
        });
    }

    @Override
    public void exception(ExceptionEvent e) {
        System.out.println("Got exception: " + e.exception());
    }

    public ThreadReference getReferenceForClass(String className) {
        return registry.getThreadReference().get();
    }

    @Override
    public synchronized void step(StepEvent e) {
        try {
            e.thread().suspend();
            if (LocationRange.ofMethod(e.location().method()).isWithin(e.location())) {
                EntryStateBuilder.ofLocation(e.location()).ifPresent(registry.getEntryListener().get()::onLocationChange);
            } else {
                registry.unregister();
            }
        } catch (AbsentInformationException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void threadDeath(ThreadDeathEvent e) {
        System.out.println("Thread death: " + e.thread());
    }

    @Override
    public void vmDeath(VMDeathEvent e) {
        System.out.println("VM death " + e);
    }

    @Override
    public void vmStart(VMStartEvent e) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting vm {}", e);
        }
    }

    /**
     * Called when a new class has been registered in the running JDI instance, and creates a breakpoint in the
     * method of that class which should be inspected. When the breakpoint is reached, it will be picked up by the
     * same {@link JdiEventHandler}.
     *
     * @param classType         The type of class that was registered.
     * @param inspectableMethod The method in the class which should be monitored.
     */
    public void registerClassInstantiation(ClassType classType, String inspectableMethod) {
        try {
            Method runMethod = classType.methodsByName(inspectableMethod).get(0);
            try {
                List<Location> lineList = new ArrayList<>(runMethod.allLineLocations());
                lineList.sort(Comparator.comparing(Location::lineNumber));
                jdi.breakpointRequest(lineList.get(0), this).enable();
            } catch (AbsentInformationException e) {
                throw new RuntimeException(e);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("No method by the name " + inspectableMethod + " found in class " +
                    classType);
        }
    }

}
