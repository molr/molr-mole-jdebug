/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package io.molr.mole.jdebug.spawner.controller;

import cern.molr.commons.mole.GenericMoleRunner;
import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.entry.EntryListener;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import org.jdiscript.JDIScript;
import org.jdiscript.util.VMLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A controller for a JDI instance that can
 */
public class JdiControllerImpl implements JdiController, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdiControllerImpl.class);
    private static final String AGENT_RUNNER_CLASS = GenericMoleRunner.class.getName();

    private final JdiEntryRegistry<EntryListener> entryRegistry;
    private final JDIScript jdi;
    private final InhibitionWrapper flowInhibitionWrapper;
    private Runnable onClose;

    private JdiControllerImpl(JDIScript jdi, JdiEntryRegistry<EntryListener> registry, InhibitionWrapper flowInhibitionWrapper) {
        this.jdi = jdi;
        this.flowInhibitionWrapper = flowInhibitionWrapper;
        entryRegistry = registry;
        jdi.vmDeathRequest(event -> {
            entryRegistry.getEntryListener().ifPresent(EntryListener::onVmDeath);
            close();
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void close() {
        entryRegistry.unregister();
        onClose.run();
    }

    public InputStream getProcessError() {
        return jdi.vm().process().getErrorStream();
    }

    @Override
    public void stepForward() {
        ThreadReference threadReference = entryRegistry.getThreadReference()
                .orElseThrow(() -> new IllegalStateException("No active entry"));
        threadReference.resume();
    }

    @Override
    public void resume() {
        flowInhibitionWrapper.stopInhibiting();
        stepForward();
    }

    @Override
    public void terminate() {
        jdi.vm().exit(0);
        entryRegistry.getEntryListener().ifPresent(EntryListener::onVmDeath);
        close();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    private static class InhibitionWrapper {
        private boolean inhibiting = true;

        public boolean isInhibiting() {
            return inhibiting;
        }

        public void stopInhibiting() {
            inhibiting = false;
        }
    }

    public static class Builder {

        private static final String CLASSPATH_PREFIX = "-cp ";

        private String classPath;
        private JdiMission jdiMission;
        private EntryListener entryListener;

        public JdiControllerImpl build() throws IOException, IllegalConnectorArgumentsException, VMStartException {
            Objects.requireNonNull(classPath, "Classpath must be set");
            Objects.requireNonNull(entryListener, "Listener must be set");
            Objects.requireNonNull(jdiMission, "Mission to inspect must be set");

            String options = CLASSPATH_PREFIX + classPath;
            final String launchArguments = jdiMission.getMoleClassName();

            final VMLauncher launcher = new VMLauncher(options, launchArguments);

            JdiEntryRegistry<EntryListener> entryRegistry = new JdiEntryRegistry<>();
            // early registration, to be notified if VM dies before reaching the first breakpoint
            entryRegistry.register(null, entryListener);

            InhibitionWrapper flowInhibitionWrapper = new InhibitionWrapper();

            JDIScript jdi = new JdiInstanceBuilder()
                    .setLauncher(launcher)
                    .setJdiMission(jdiMission)
                    .setEntryRegistry(entryRegistry)
                    .setListener(entryListener)
                    .setFlowInhibitor(whatever -> flowInhibitionWrapper.isInhibiting())
                    .build();

            return new JdiControllerImpl(jdi, entryRegistry, flowInhibitionWrapper);
        }

        public Builder setEntryListener(EntryListener entryListener) {
            this.entryListener = entryListener;
            return this;
        }

        public Builder setClassPath(String path) {
            this.classPath = path;
            return this;
        }

        public Builder setJdiMission(JdiMission method) {
            this.jdiMission = method;
            return this;
        }
    }
}
