package cern.molr.jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that encapsulates the behaviour for spawning a new JVM using the {@link ProcessBuilder}
 *
 * @author tiagomr
 */
public abstract class AbstractJvmSpawner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJvmSpawner.class);
    public static final String JAVA_HOME_PROPERTY_NAME = "java.home";

    protected Process spawnJvm(List<String> arguments) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(String.format("%s/bin/java", System.getProperty(JAVA_HOME_PROPERTY_NAME)));
        command.addAll(arguments);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        LOGGER.info("Starting JVM with parameters: [{}]", command.toString());
        return processBuilder.start();
    }
}
