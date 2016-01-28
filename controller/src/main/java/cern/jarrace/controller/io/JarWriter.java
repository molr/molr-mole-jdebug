package cern.jarrace.controller.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by timartin on 28/01/2016.
 */
public class JarWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarWriter.class);

    @Value("${io.deploymentdir}")
    private String deployment_dir;

    public String writeFile(String name, byte[] jar) throws IOException {
        File deploymentFile = Paths.get(deployment_dir).resolve(name + ".jar").toFile();
        LOGGER.info("Writing file [{}]", deploymentFile.getAbsolutePath());
        if (deploymentFile.exists()) {
            deploymentFile.delete();
        }
        deploymentFile.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(deploymentFile);
        outputStream.write(jar);
        outputStream.close();
        return deploymentFile.getAbsolutePath();
    }
}
