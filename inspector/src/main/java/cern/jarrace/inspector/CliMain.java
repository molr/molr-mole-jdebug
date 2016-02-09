/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector;

import cern.jarrace.commons.instantiation.InstantiationRequest;
import cern.jarrace.commons.instantiation.JsonInstantiationRequest;
import cern.jarrace.inspector.controller.JdiController;
import cern.jarrace.inspector.controller.JdiControllerImpl;
import cern.jarrace.inspector.remote.EntryListenerWriter;
import cern.jarrace.inspector.remote.JdiControllerReader;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class containing a main method which can instantiate a {@link cern.jarrace.inspector.controller.JdiController}
 * in a command line interface environment.
 */
public class CliMain implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CliMain.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private JdiController controller;
    private JdiControllerReader commandReader;
    private EntryListenerWriter entryWriter;

    public CliMain(JdiControllerImpl controller, JdiControllerReader commandReader, EntryListenerWriter entryWriter) {
        this.controller = controller;
        this.commandReader = commandReader;
        this.entryWriter = entryWriter;
        executor.submit(() -> {
            Process process = controller.getProcess();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            LOGGER.error("Expected 1 argument, but received " + args.length);
        } else {
            final JsonInstantiationRequest request = JsonInstantiationRequest.fromJson(args[0]);
            final JdiControllerImpl controller = startJdi(request);
            final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
            final PrintWriter outputWriter = new PrintWriter(System.out);
            final JdiControllerReader reader = new JdiControllerReader(inputReader, controller);
            final EntryListenerWriter writer = new EntryListenerWriter(outputWriter);
            final CliMain main = new CliMain(controller, reader, writer);
            controller.setOnClose(main::close);
        }
    }

    private static JdiControllerImpl startJdi(InstantiationRequest request) throws Exception {
        try {
            return JdiControllerImpl.builder()
                    .setClassPath(request.getClassPath())
                    .setService(request.getEntryPoints())
                    .build();
        } catch (IllegalConnectorArgumentsException e) {
            LOGGER.warn("Bad connection parameters {} when starting JDI", request, e);
            throw e;
        } catch (Exception e) {
            LOGGER.warn("Failure when starting JDI instance", e);
            throw e;
        }
    }

    @Override
    public void close() {
        controller.terminate();
        commandReader.close();
        entryWriter.close();
        executor.shutdown();
    }
}
