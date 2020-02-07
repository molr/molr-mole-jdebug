package io.molr.mole.jdebug.spawner;

import cern.molr.commons.annotations.Source;
import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.controller.StatefulJdiControllerImpl;
import io.molr.mole.jdebug.spawner.domain.InstantiationRequest;
import io.molr.mole.jdebug.spawner.domain.Session;
import io.molr.mole.jdebug.spawner.domain.SourceFetcher;
import io.molr.mole.jdebug.spawner.domain.impl.InstantiationRequestImpl;
import io.molr.mole.jdebug.spawner.domain.impl.SessionImpl;
import io.molr.mole.jdebug.spawner.json.MissionTypeAdapter;
import io.molr.mole.jdebug.spawner.remote.SystemMain;
import cern.molr.jvm.JvmSpawnHelper;
import cern.molr.jvm.MoleSpawner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;
import java.util.concurrent.Executors;

/**
 * @author timartin
 */
public class DebugMoleSpawner implements MoleSpawner<Session>, SourceFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugMoleSpawner.class);
    public static final String CURRENT_CLASSPATH_VALUE = System.getProperty("java.class.path");
    private static final String INSPECTOR_MAIN_CLASS = SystemMain.class.getName();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(JdiMission.class, new MissionTypeAdapter().nullSafe())
            .create();

    public String getSource(String classname) {
        try {
            Source classSource = (Source) Class.forName(classname + "Source").newInstance();
            String base64Source = classSource.base64Value();
            return new String(Base64.getDecoder().decode(base64Source));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException exception) {
            LOGGER.error("Could not load source code for class", classname, exception);
            return "";
        }
    }

    @Override
    public Session spawnMoleRunner(JdiMission jdiMission, String... args) throws IOException {
        if (jdiMission == null) {
            throw new IllegalArgumentException("The mission must not be null");
        }
        InstantiationRequest request = new InstantiationRequestImpl(CURRENT_CLASSPATH_VALUE, jdiMission);
        String[] completedArgs = new String[args.length + 1];
        completedArgs[0] = GSON.toJson(request);
        int i = 1;
        for (String arg : args) {
            completedArgs[i++] = arg;
        }

        Process process = JvmSpawnHelper.getProcessBuilder(
                JvmSpawnHelper.appendToolsJarToClasspath(request.getClassPath()),
                INSPECTOR_MAIN_CLASS,
                completedArgs).start();
        redirectStream(process.getErrorStream(), System.err);
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

        return new SessionImpl(jdiMission, new StatefulJdiControllerImpl(process));
    }

    @Override
    public Session spawnMoleRunner(JdiMission jdiMission, String classpath, String... args) throws Exception {
        return null;
    }

    private static void redirectStream(InputStream input, PrintStream output) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(input))) {
                while (true) {
                    final String line = errorReader.readLine();
                    if (line != null) {
                        output.println(line);
                    }
                }
            } catch (IOException e) {
                output.println("Error when reading from process: " + e);
            }
        });
    }



}
