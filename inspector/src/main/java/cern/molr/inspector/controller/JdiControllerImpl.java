/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.controller;

import cern.molr.commons.domain.Service;
import cern.molr.inspector.entry.EntryListener;
import cern.molr.inspector.entry.EntryListenerFactory;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
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
import java.util.Optional;

/**
 * A controller for a JDI instance that can
 */
public class JdiControllerImpl implements JdiController, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdiControllerImpl.class);

    private static final String AGENT_RUNNER_CLASS = "cern.jarrace.mole.AgentRunner";

    private final JdiEntryRegistry<EntryListener> entryRegistry;
    private final JDIScript jdi;
    private Runnable onClose;

    private JdiControllerImpl(JDIScript jdi, JdiEntryRegistry<EntryListener> registry) {
        this.jdi = jdi;
        entryRegistry = registry;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void close() {
        try {
            jdi.vmDeathRequest(event -> entryRegistry.getEntryListener().ifPresent(EntryListener::onVmDeath));
        } catch (VMDisconnectedException e) {
            /* Already disconnected */
        }
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
    public void terminate() {
        Optional<EntryListener> entryListener = entryRegistry.getEntryListener();
        Optional<ThreadReference> threadReference = entryRegistry.getThreadReference();
        if (entryListener.isPresent() && threadReference.isPresent()) {
            closeThread(threadReference.get(), entryListener.get());
        }
        close();
    }

    private void closeThread(ThreadReference thread, EntryListener listener) {

    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public static class Builder {

        private static final String CLASSPATH_PREFIX = "-cp ";

        private String classPath;
        private Service service;
        private EntryListenerFactory<?> factory;

        public JdiControllerImpl build() throws IOException, IllegalConnectorArgumentsException, VMStartException {
            Objects.requireNonNull(classPath, "Classpath must be set");
            Objects.requireNonNull(factory, "Listener factory must be set");
            Objects.requireNonNull(service, "Service to inspect must be set");

            final String launchArguments = AGENT_RUNNER_CLASS + " " + service.getMoleClassName() + " " + service.getServiceClassName();
            final VMLauncher launcher = new VMLauncher(CLASSPATH_PREFIX + classPath, launchArguments);

            JdiEntryRegistry<EntryListener> entryRegistry = new JdiEntryRegistry<>();

            JDIScript jdi = new JdiInstanceBuilder()
                    .setLauncher(launcher)
                    .setService(service)
                    .setEntryRegistry(entryRegistry)
                    .setListenerFactory(factory)
                    .build();

            return new JdiControllerImpl(jdi, entryRegistry);
        }

        public Builder setListenerFactory(EntryListenerFactory<?> factory) {
            this.factory = factory;
            return this;
        }

        public Builder setClassPath(String path) {
            this.classPath = path;
            return this;
        }

        public Builder setService(Service method) {
            this.service = method;
            return this;
        }

    }

}
