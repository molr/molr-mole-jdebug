/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller;

import cern.jarrace.inspector.jdi.EntryState;
import cern.jarrace.inspector.entry.BlockingCallbackFactory;
import cern.jarrace.inspector.entry.BlockingEntryListener;
import cern.jarrace.inspector.entry.EntryMethod;
import cern.jarrace.inspector.jdi.ClassInstantiationListener;
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
import java.util.Optional;
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
    public void close() {
        try {
            jdi.vm().exit(0);
            executorService.shutdown();
        } catch (VMDisconnectedException e) {
            /* Already disconnected */
        }
    }

    @Override
    public EntryState stepForward(String entry) {
        ThreadReference threadReference = entryRegistry.getThreadReference(entry)
                .orElseThrow(() -> new IllegalArgumentException("No active entry called " + entry));
        BlockingEntryListener listener = entryRegistry.getEntryListener(entry).get();

        clearStepCallbacks();
        threadReference.resume();

        try {
            return listener.waitForNextEntry();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static class Builder {

        private static final String CLASSPATH_PREFIX = "-cp ";

        private String classPath;
        private String mainClass;
        private EntryMethod inspectableMethod;

        public BlockingJdiController build() throws IOException, IllegalConnectorArgumentsException, VMStartException {
            Objects.requireNonNull(classPath, "Classpath must be set");
            Objects.requireNonNull(mainClass, "Entry class with a main method must be set");
            Objects.requireNonNull(inspectableMethod, "Method to inspect must be set");
            final Class<?> methodClass = inspectableMethod.getMethodClass();
            final VMLauncher launcher = new VMLauncher(CLASSPATH_PREFIX + classPath, mainClass);

            VirtualMachine virtualMachine = launcher.safeStart();
            JDIScript jdi = new JDIScript(virtualMachine);
            JdiEntryRegistry<BlockingEntryListener> entryRegistry = new JdiEntryRegistry<>();
            BlockingCallbackFactory callbackFactory = new BlockingCallbackFactory(entryRegistry);
            JdiEventHandler eventHandler = new JdiEventHandlerImpl(jdi, callbackFactory);

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(() -> {
                ClassInstantiationListener instantiationListener =
                        new ClassInstantiationListener(methodClass,
                                classType -> register(jdi, eventHandler, classType, inspectableMethod));
                jdi.onClassPrep(instantiationListener);

                jdi.run(eventHandler);
            });

            return new BlockingJdiController(jdi, eventHandler, entryRegistry, executorService);
        }

        public Builder setInspectableMethod(EntryMethod method) {
            this.inspectableMethod = method;
            return this;
        }

        public Builder setClassPath(String path) {
            this.classPath = path;
            return this;
        }

        public Builder setMainClass(String mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        public static void register(JDIScript jdi, JdiEventHandler eventHandler,
                                    ClassType classType, EntryMethod inspectableMethod) {
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
