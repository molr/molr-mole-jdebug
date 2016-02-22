/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.controller;

import cern.molr.commons.domain.Mission;
import cern.molr.inspector.entry.EntryListener;
import cern.molr.inspector.entry.EntryListenerFactory;
import cern.molr.inspector.jdi.ClassInstantiationListener;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import org.jdiscript.JDIScript;
import org.jdiscript.util.VMLauncher;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A builder to help create and spawn running VM's {@link JdiInstanceBuilder}.
 */
public class JdiInstanceBuilder {

    private VMLauncher launcher;
    private Mission mission;
    private EntryListenerFactory<?> listenerFactory;
    private JdiEntryRegistry<EntryListener> registry;

    /**
     * Builds a new {@link JDIScript} by 1) asking the {@link VMLauncher} to launch a new process,
     * 2) creates a listener to listen for the new instantiations of the {@link Mission} classes given in the
     * builder and 3) asks the running VM to break whenever such a mission is met.
     *
     * @return An instantiation of a {@link JDIScript}.
     * @throws IOException If the {@link VMLauncher} fails to launch the process.
     */
    public JDIScript build() throws IOException {
        Objects.requireNonNull(launcher, "Launcher must not be null");
        Objects.requireNonNull(mission, "Service must not be null");
        Objects.requireNonNull(registry, "Entry registry must be set");
        Objects.requireNonNull(listenerFactory, "Listener factory must not be null");

        try {
            VirtualMachine virtualMachine = launcher.safeStart();
            JDIScript jdi = new JDIScript(virtualMachine);
            SteppingJdiEventHandler eventHandler = new SteppingJdiEventHandler(jdi, listenerFactory, registry);

            ClassInstantiationListener instantiationListener =
                    new ClassInstantiationListener(mission.getMissionContentClassName(),
                            classType -> mission.getTasksNames().forEach(methodName ->
                                    eventHandler.registerClassInstantiation(classType, methodName)));

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.submit(() -> {
                jdi.onClassPrep(instantiationListener);
                jdi.run(eventHandler);
            });
            executorService.shutdown();
            return jdi;
        } catch (VMStartException | IllegalConnectorArgumentsException e) {
            throw new IOException("Failed to start VM: ", e);
        }
    }


    public JdiInstanceBuilder setEntryRegistry(JdiEntryRegistry<EntryListener> registry) {
        this.registry = registry;
        return this;
    }

    public JdiInstanceBuilder setListenerFactory(EntryListenerFactory<?> listenerFactory) {
        this.listenerFactory = listenerFactory;
        return this;
    }

    public JdiInstanceBuilder setLauncher(VMLauncher launcher) {
        this.launcher = launcher;
        return this;
    }

    public JdiInstanceBuilder setMission(Mission mission) {
        this.mission = mission;
        return this;
    }

}
