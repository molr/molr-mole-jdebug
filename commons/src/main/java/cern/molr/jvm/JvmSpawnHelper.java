package cern.molr.jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class that encapsulates the behaviour for spawning a new JVM using the {@link ProcessBuilder}
 *
 * @author tiagomr
 */
public final class JvmSpawnHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JvmSpawnHelper.class);

    private static final String CLASSPATH_ARGUMENT_INDICATOR = "-cp";
    public static final String JAVA_HOME_PROPERTY_NAME = "java.home";

    private JvmSpawnHelper(){
    }

    public static final ProcessBuilder getProcessBuilder(String classpath, String mainClass, String... arguments) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(String.format("%s/bin/java", System.getProperty(JAVA_HOME_PROPERTY_NAME)));
        command.add(CLASSPATH_ARGUMENT_INDICATOR);
        command.add(classpath);
        command.add(mainClass);
        if(arguments != null) {
            command.addAll(Arrays.asList(arguments));
        }
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        LOGGER.info("Starting JVM with parameters: [{}]", command.toString());
        return processBuilder;
    }
}
