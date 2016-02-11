/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector;

import cern.jarrace.commons.domain.Service;
import cern.jarrace.inspector.controller.JdiController;
import cern.jarrace.inspector.entry.EntryListener;
import cern.jarrace.inspector.remote.EntryListenerReader;
import cern.jarrace.inspector.remote.JdiControllerWriter;
import cern.molr.inspector.domain.InstantiationRequest;
import cern.molr.inspector.json.ServiceTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.concurrent.Executors;

/**
 * The main entry-ooint for using the inspector library. The {@link #instance(InstantiationRequest, EntryListener)}
 * method spawns and connects to running inspector instances which can be controlled through the returned
 * {@link JdiController} interface. The running VM will reply to the given {@link EntryListener} whenever instances
 * of the requested {@link Service} in the {@link InstantiationRequest} is being stepped through.
 */
public enum Inspect {

    /**
     * This instance spawns new inspector instances locally.
     */
    LOCAL(new ProcessInstantiator());

    private final Instantiator instantiator;

    Inspect(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    public JdiController instance(InstantiationRequest request, EntryListener listener) throws IOException {
        return instantiator.instantiate(request, listener);
    }

    private interface Instantiator {

        JdiController instantiate(InstantiationRequest request, EntryListener listener) throws IOException;

    }

    private static final class ProcessInstantiator implements Instantiator {
        private static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(Service.class, new ServiceTypeAdapter().nullSafe())
                .create();

        private static final String INSPECTOR_MAIN_CLASS = "cern.jarrace.inspector.remote.SystemMain";

        @Override
        public JdiController instantiate(InstantiationRequest request, EntryListener listener) throws IOException {
            ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/java", "-cp", request.getClassPath(), INSPECTOR_MAIN_CLASS, GSON.toJson(request));
//            ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/java", "-cp", request.getClassPath(), INSPECTOR_MAIN_CLASS, "{\"classPath\":\"/local/acalia/test.jar\",\"service\":{\"agentName\":\"cern.jarrace.agent.impl.RunnableAgent\",\"className\":\"cern.lhc.opcoupling.mole.procedure.Beam1ScanProcedure\",\"entryPoints\":\"run\"}}");
            System.err.println(processBuilder.command()); // TODO
            Process process = processBuilder.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                process.destroy();
            }));
            
            EntryListenerReader listenerReader = new EntryListenerReader(new BufferedReader(new InputStreamReader(process.getInputStream())), listener);
            JdiControllerWriter writer = new JdiControllerWriter(new PrintWriter(process.getOutputStream()));
            redirectError(process.getErrorStream(), System.err);
            return writer;
        }
    }

    private static void redirectError(InputStream errorInput, PrintStream out) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorInput))) {
                while (true) {
                    final String line = errorReader.readLine();
                    if (line != null) {
                        out.println(line);
                    }
                }
            } catch (IOException e) {
                out.println("Error when reading from process: " + e);
            }
        });
    }

}
