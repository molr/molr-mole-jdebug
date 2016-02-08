/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller;

import cern.jarrace.commons.domain.Service;
import cern.jarrace.inspector.controller.factory.JdiFactory;
import cern.jarrace.inspector.controller.factory.JdiFactory.JdiFactoryInstance;
import cern.jarrace.inspector.entry.BlockingCallbackFactory;
import cern.jarrace.inspector.entry.BlockingEntryListener;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import org.jdiscript.JDIScript;
import org.jdiscript.handlers.OnVMDeath;
import org.jdiscript.util.VMLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

/**
 * A controller for a JDI instance that can
 */
public class JdiControllerImpl implements JdiController, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdiControllerImpl.class);

    private final JdiEntryRegistry<BlockingEntryListener> entryRegistry;
    private final JDIScript jdi;
    private final JdiEventHandler eventHandler;
    private Runnable onClose;

    private JdiControllerImpl(JDIScript jdi, JdiEventHandler eventHandler, JdiEntryRegistry<BlockingEntryListener> entryRegistry) {
        this.jdi = jdi;
        this.eventHandler = eventHandler;
        this.entryRegistry = entryRegistry;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void close() {
        try {
            jdi.vmDeathRequest(death -> {
                LOGGER.debug("Closed vm {}", death);
            });
        } catch (VMDisconnectedException e) {
            /* Already disconnected */
        }
        onClose.run();
    }

    @Override
    public void stepForward() {
        ThreadReference threadReference = entryRegistry.getThreadReference()
                .orElseThrow(() -> new IllegalStateException("No active entry"));
        threadReference.resume();
    }

    @Override
    public void terminate() {
        close();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public static class Builder {

        private static final String CLASSPATH_PREFIX = "-cp ";

        private String classPath;
        private String mainClass;
        private Service service;
        private OnVMDeath onShutdown;

        public JdiControllerImpl build() throws IOException, IllegalConnectorArgumentsException, VMStartException {
            Objects.requireNonNull(classPath, "Classpath must be set");
            Objects.requireNonNull(mainClass, "Entry class with a main method must be set");
            Objects.requireNonNull(service, "Service to inspect must be set");
            final VMLauncher launcher = new VMLauncher(CLASSPATH_PREFIX + classPath, mainClass);

            JdiEntryRegistry<BlockingEntryListener> entryRegistry = new JdiEntryRegistry<>();
            BlockingCallbackFactory callbackFactory = new BlockingCallbackFactory(entryRegistry);

            JdiFactoryInstance instance = JdiFactory
                    .withLauncher(launcher)
                    .spawnJdi(service, callbackFactory);

            return new JdiControllerImpl(instance.getJdi(), instance.getEventHandler(), entryRegistry);
        }

        public Builder setService(Service method) {
            this.service = method;
            return this;
        }

        public Builder setClassPath(String path) {
            this.classPath = path;
            return this;
        }

        public Builder setOnShutdown(OnVMDeath onShutdown) {
            this.onShutdown = onShutdown;
            return this;
        }

    }

}
