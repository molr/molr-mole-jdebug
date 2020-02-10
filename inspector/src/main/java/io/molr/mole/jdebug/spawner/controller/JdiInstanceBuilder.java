/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package io.molr.mole.jdebug.spawner.controller;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.entry.EntryListener;
import io.molr.mole.jdebug.spawner.jdi.ClassInstantiationListener;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.StepEvent;
import org.jdiscript.JDIScript;
import org.jdiscript.util.VMLauncher;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/**
 * A builder to help create and spawn running VM's {@link JdiInstanceBuilder}.
 */
public class JdiInstanceBuilder {

    private VMLauncher launcher;
    private JdiMission jdiMission;
    private Predicate<StepEvent> flowInhibitor;
    private EntryListener entryListener;
    private JdiEntryRegistry<EntryListener> registry;

    /**
     * Builds a new {@link JDIScript} by 1) asking the {@link VMLauncher} to launch a new process,
     * 2) creates a listener to listen for the new instantiations of the {@link JdiMission} classes given in the
     * builder and 3) asks the running VM to break whenever such a mission is met.
     *
     * @return An instantiation of a {@link JDIScript}.
     * @throws IOException If the {@link VMLauncher} fails to launch the process.
     */
    public JDIScript build() throws IOException {
        Objects.requireNonNull(launcher, "Launcher must not be null");
        Objects.requireNonNull(jdiMission, "Service must not be null");
        Objects.requireNonNull(registry, "Entry registry must be set");
        Objects.requireNonNull(entryListener, "Listener must not be null");
        Objects.requireNonNull(flowInhibitor, "Flow inhibitor must not be null");

        try {
            VirtualMachine virtualMachine = launcher.safeStart();
            JDIScript jdi = new JDIScript(virtualMachine);
            SteppingJdiEventHandler eventHandler = new SteppingJdiEventHandler(jdi, entryListener, registry, flowInhibitor);

           Runtime.getRuntime().addShutdownHook(new Thread(virtualMachine.process()::destroy));

            ClassInstantiationListener instantiationListener =
                    new ClassInstantiationListener(jdiMission.getMissionContentClassName(),
                            classType -> jdiMission.getTasksNames().forEach(methodName ->
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

    public JdiInstanceBuilder setListener(EntryListener entryListener) {
        this.entryListener = entryListener;
        return this;
    }

    public JdiInstanceBuilder setLauncher(VMLauncher launcher) {
        this.launcher = launcher;
        return this;
    }

    public JdiInstanceBuilder setJdiMission(JdiMission jdiMission) {
        this.jdiMission = jdiMission;
        return this;
    }

    public JdiInstanceBuilder setFlowInhibitor(Predicate<StepEvent> flowInhibitor) {
        this.flowInhibitor = flowInhibitor;
        return this;
    }

}
