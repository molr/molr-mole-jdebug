package cern.molr.inspector;

import cern.molr.commons.domain.Service;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.domain.InstantiationRequest;
import cern.molr.inspector.domain.InstantiationRequestImpl;
import cern.molr.inspector.entry.EntryListener;
import cern.molr.inspector.json.ServiceTypeAdapter;
import cern.molr.inspector.remote.EntryListenerReader;
import cern.molr.inspector.remote.JdiControllerWriter;
import cern.molr.jvm.JvmSpawnHelper;
import cern.molr.jvm.MoleSpawner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author timartin
 */
public class DebugMoleSpawner implements MoleSpawner<JdiController> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugMoleSpawner.class);
    private static final String CURRENT_CLASSPATH_VALUE = System.getProperty("java.class.path");
    private static final String INSPECTOR_MAIN_CLASS = "SystemMain";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Service.class, new ServiceTypeAdapter().nullSafe())
            .create();

    @Override
    public JdiController spawnMoleRunner(Service service, String... args) throws Exception {
        InstantiationRequest request = new InstantiationRequestImpl(System.getProperty("java.class.path"), service);
        String[] completedArgs = new String[args.length + 1];
        completedArgs[0] = GSON.toJson(request);
        int i = 1;
        for(String arg : args) {
            completedArgs[i++] = arg;
        }
        Process process = JvmSpawnHelper.getProcessBuilder(request.getClassPath(), INSPECTOR_MAIN_CLASS, completedArgs).start();
        redirectError(process.getErrorStream(), System.err);
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        return new MyJdiControllerWriter(process);
    }

    private static class MyJdiControllerWriter extends JdiControllerWriter {

        private final Process process;

        public MyJdiControllerWriter(Process process) {
            super(new PrintWriter(process.getOutputStream()));
            this.process = process;
        }

        @Override
        public void setEntryListener(EntryListener entryListener) {
            EntryListenerReader listenerReader = new EntryListenerReader(new BufferedReader(new InputStreamReader(process.getInputStream())), entryListener);
            listenerReader.setOnClose(process::destroy);
        }
    }

    @Override
    public JdiController spawnMoleRunner(Service service, String classpath, String... args) throws Exception {
        return null;
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

    /*
     private static final class ProcessInstantiator implements Instantiator {
        private static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(Service.class, new ServiceTypeAdapter().nullSafe())
                .create();

        private static final String INSPECTOR_MAIN_CLASS = "SystemMain";

        @Override
        public JdiController instantiate(InstantiationRequest request, EntryListener listener) throws IOException {
            ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/java", "-cp", request.getClassPath(), INSPECTOR_MAIN_CLASS, GSON.toJson(request));
            Process process = processBuilder.start();

            Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

            JdiControllerWriter writer = new JdiControllerWriter(new PrintWriter(process.getOutputStream()));
            redirectError(process.getErrorStream(), System.err);
            EntryListenerReader listenerReader = new EntryListenerReader(new BufferedReader(new InputStreamReader(process.getInputStream())), listener);
            listenerReader.setOnClose(process::destroy);

            return writer;
        }
    }
     */
}
