/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller;

import cern.jarrace.inspector.EntryState;
import cern.jarrace.inspector.entry.BlockingCallbackFactory;
import cern.jarrace.inspector.entry.BlockingEntryListener;
import cern.jarrace.inspector.entry.InspectableMethod;
import cern.jarrace.inspector.entry.InterfaceImplementationListener;
import com.sun.jdi.*;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.request.StepRequest;
import org.jdiscript.JDIScript;
import org.jdiscript.requests.ChainingStepRequest;
import org.jdiscript.util.VMLauncher;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A controller for a JDI instance that can
 */
public class BlockingJdiController implements JdiController, Closeable {

    private final JdiEntryRegistry<BlockingEntryListener> entryRegistry;
    private final ExecutorService executorService;
    private final JDIScript jdi;
    private final JdiEventHandler eventHandler;

    private BlockingJdiController(JDIScript jdi, JdiEventHandler eventHandler, JdiEntryRegistry<BlockingEntryListener> entryRegistry, ExecutorService executorService) {
        this.jdi = jdi;
        this.eventHandler = eventHandler;
        this.entryRegistry = entryRegistry;
        this.executorService = executorService;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void clearStepCallbacks() {
        jdi.stepRequests(eventHandler).clear();
    }

    @Override
    public void close() throws IOException {
        try {
            jdi.vm().exit(0);
            executorService.shutdown();
        } catch (VMDisconnectedException e) {
            /* Already disconnected */
        }
    }

    @Override
    public EntryState stepForward(String entry) {
        ThreadReference threadReference = entryRegistry.getThreadReference(entry);
        if (threadReference == null) {
            throw new IllegalArgumentException("No active entry called " + entry);
        }
        BlockingEntryListener listener = entryRegistry.getEntryListener(entry);

        clearStepCallbacks();
        ChainingStepRequest stepRequest = jdi
                .stepRequest(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        stepRequest.enable();

        try {
            return listener.waitForNextEntry();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static class Builder {

        private VMLauncher launcher;
        private InspectableMethod inspectableMethod;
        private Class<?> interfaceToListen;
        private BlockingCallbackFactory callbackFactory;

        public BlockingJdiController build() throws IOException, IllegalConnectorArgumentsException, VMStartException {
            Objects.requireNonNull(launcher, "Launcher must be set");
            Objects.requireNonNull(inspectableMethod, "Method to inspect must be set");

            VirtualMachine virtualMachine = launcher.safeStart();
            JDIScript jdi = new JDIScript(virtualMachine);
            JdiEntryRegistry<BlockingEntryListener> entryRegistry = new JdiEntryRegistry<>();
            BlockingCallbackFactory callbackFactory = new BlockingCallbackFactory(entryRegistry);
            JdiEventHandler eventHandler = new JdiEventHandlerImpl(jdi, callbackFactory);

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(() -> {
                InterfaceImplementationListener interfaceImplementationCounter = new InterfaceImplementationListener(
                        interfaceToListen, classType -> {
                    register(jdi, eventHandler, classType, inspectableMethod);
                });
                jdi.onClassPrep(interfaceImplementationCounter);

                jdi.run(eventHandler);
            });

            return new BlockingJdiController(jdi, eventHandler, entryRegistry, executorService);
        }

        public Builder setLauncher(VMLauncher launcher) {
            this.launcher = launcher;
            return this;
        }

        public Builder setInspectableMethod(InspectableMethod method) {
            this.inspectableMethod = method;
            return this;
        }

        public static void register(JDIScript jdi, JdiEventHandler eventHandler,
                                    ClassType classType, InspectableMethod inspectableMethod) {
            Method runMethod = classType.methodsByName(inspectableMethod.getMethodName()).get(0);
            try {
                List<Location> lineList = new ArrayList<>(runMethod.allLineLocations());
                lineList.sort((line1, line2) -> Integer.compare(line1.lineNumber(), line2.lineNumber()));
                setBreakpoint(jdi, eventHandler, lineList.get(0));
            } catch (AbsentInformationException e) {
                throw new RuntimeException(e);
            }
        }

        private static void setBreakpoint(JDIScript jdi, JdiEventHandler eventHandler, Location location) {
            jdi.breakpointRequest(location, eventHandler).enable();
        }

    }

}
