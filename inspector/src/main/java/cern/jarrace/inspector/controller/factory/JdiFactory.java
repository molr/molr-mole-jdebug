/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller.factory;

import cern.jarrace.inspector.controller.JdiEventHandler;
import cern.jarrace.inspector.controller.SteppingJdiEventHandler;
import cern.jarrace.inspector.entry.EntryListener;
import cern.jarrace.inspector.entry.EntryListenerFactory;
import cern.jarrace.inspector.jdi.ClassInstantiationListener;
import com.sun.jdi.*;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import org.jdiscript.JDIScript;
import org.jdiscript.util.VMLauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A factory to instantiate running JDI instances.
 */
public class JdiFactory {

    private final VMLauncher launcher;

    private JdiFactory(VMLauncher launcher) {
        this.launcher = launcher;
    }

    public static void register(JDIScript jdi, JdiEventHandler eventHandler,
                                ClassType classType, String inspectableMethod) {
        Method runMethod = classType.methodsByName(inspectableMethod).get(0);
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

    public <Listener extends EntryListener> JdiFactoryInstance spawnJdi(
            String className, List<String> entryPoints, EntryListenerFactory<Listener> callbackFactory) throws IOException {
        try {
            VirtualMachine virtualMachine = launcher.safeStart();
            JDIScript jdi = new JDIScript(virtualMachine);
            SteppingJdiEventHandler eventHandler = new SteppingJdiEventHandler(jdi, callbackFactory);

            ClassInstantiationListener instantiationListener =
                    new ClassInstantiationListener(className,
                            classType -> entryPoints.forEach(methodName ->
                                    register(jdi, eventHandler, classType, methodName)));

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(() -> {
                jdi.onClassPrep(instantiationListener);
                jdi.run(eventHandler);
            });
            return new JdiFactoryInstance(jdi, eventHandler);
        } catch (VMStartException | IllegalConnectorArgumentsException e) {
            throw new IOException("Failed to starte VM: ", e);
        }
    }

    public static JdiFactory withLauncher(VMLauncher launcher) {
        return new JdiFactory(launcher);
    }

    /**
     * An running instance of a JDI with access to the {@link JDIScript} and {@link JdiEventHandler}.
     */
    public static class JdiFactoryInstance {

        private final JDIScript jdi;
        private final JdiEventHandler eventHandler;

        private JdiFactoryInstance(JDIScript jdi, JdiEventHandler eventHandler) {
            this.jdi = jdi;
            this.eventHandler = eventHandler;
        }

        /**
         * @return The JDI instance.
         */
        public JDIScript getJdi() {
            return jdi;
        }

        /**
         * @return The event handler attached to a running JDI instance.
         */
        public JdiEventHandler getEventHandler() {
            return eventHandler;
        }

    }

}
