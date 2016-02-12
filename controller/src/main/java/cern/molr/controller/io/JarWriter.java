/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Class responsible of writing the deployment file to the file system
 *
 * @author tiagomr
 */
public class JarWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarWriter.class);
    private static final String FILE_EXTENSION = "jar";
    private static final String IO_DEPLOYMENT_DIR_PROPERTY_NAME = "{io.deploymentdir";

    @Value("$" + IO_DEPLOYMENT_DIR_PROPERTY_NAME + "}")
    private String deploymentPath;
    private Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    @PostConstruct
    void init() throws InvalidPropertyException {
        if (deploymentPath == null || deploymentPath.isEmpty()) {
            throw new InvalidPropertyException(JarWriter.class, IO_DEPLOYMENT_DIR_PROPERTY_NAME, "Property cannot be null nor empty");
        }
        File deploymentDir = new File(deploymentPath);
        checkDirectory(deploymentDir);
    }

    private void checkDirectory(File deploymentDir) {
        if (!deploymentDir.exists()) {
            if (!deploymentDir.mkdirs()) {
                throw new IllegalStateException(String.format("Deployment folder cannot be created: [%s]",
                        deploymentPath));
            }
        } else if (!deploymentDir.isDirectory()) {
            throw new InvalidPropertyException(JarWriter.class, "deployment_dir", "Property points to a file and not a directory.");
        }
    }

    public String writeFile(String name, byte[] jar) throws IOException, IllegalAccessException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name of the deployment cannot be null nor empty");
        }

        if (!pattern.matcher(name).matches()) {
            throw new IllegalArgumentException("The name can be composed only by alphanenumeric characteres");
        }

        if (jar == null || jar.length == 0) {
            throw new IllegalArgumentException("Deployment file cannot be null nor empty");
        }

        File deploymentFile = Paths.get(deploymentPath).resolve(String.format("%s.%s", name, FILE_EXTENSION)).toFile();
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

    // Used for testing
    void setDeploymentPath(String deploymentPath) {
        this.deploymentPath = deploymentPath;
    }
}
