/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package io.molr.mole.jdebug.spawner.remote;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.controller.JdiController;
import io.molr.mole.jdebug.spawner.controller.JdiControllerImpl;
import io.molr.mole.jdebug.spawner.domain.InstantiationRequest;
import io.molr.mole.jdebug.spawner.domain.impl.InstantiationRequestImpl;
import io.molr.mole.jdebug.spawner.entry.EntryListener;
import io.molr.mole.jdebug.spawner.json.MissionTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An entry point for creating a {@link JdiController} which communicates via
 * {@link System#in} and {@link System#out}. {@link System#err} is used to communicate errors from the process.
 */
public class SystemMain implements Closeable {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(JdiMission.class, new MissionTypeAdapter().nullSafe())
            .create();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private EntryListenerWriter entryWriter;

    private final Future<?> loggerTask;

    public SystemMain(JdiControllerImpl controller, EntryListenerWriter entryWriter) {
        this.entryWriter = entryWriter;
        this.loggerTask = executor.submit(() -> {
            InputStream processError = controller.getProcessError();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(processError))) {
                while (!Thread.interrupted()) {
                    logLine(errorReader);
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        });

        controller.setOnClose(this::close);
    }

    private static void logLine(BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line != null) {
            System.err.println(line);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Expected 1 argument, but received " + args.length);
        } else {
            InstantiationRequest request = GSON.fromJson(args[0], InstantiationRequestImpl.class);
            create(request);
        }
    }

    public static SystemMain create(InstantiationRequest request) {
        PrintWriter outputWriter = new PrintWriter(System.out);
        EntryListenerWriter writer = new EntryListenerWriter(outputWriter);

        JdiControllerImpl controller = startJdi(request, writer);
        new JdiControllerReader(new BufferedReader(new InputStreamReader(System.in)), controller);
        return new SystemMain(controller, writer);
    }

    public static JdiControllerImpl startJdi(InstantiationRequest request, EntryListener entryListener) {
        try {
            return JdiControllerImpl.builder()
                    .setClassPath(request.getClassPath())
                    .setEntryListener(entryListener)
                    .setJdiMission(request.getJdiMission())
                    .build();
        } catch (IllegalConnectorArgumentsException e) {
            throw new RuntimeException("Bad connection parameters " + request + " when starting JDI.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failure when starting JDI instance.", e);
        }
    }

    @Override
    public void close() {
        entryWriter.close();
        loggerTask.cancel(true);
        executor.shutdown();
        System.exit(0);
    }
}
