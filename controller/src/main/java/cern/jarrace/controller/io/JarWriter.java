package cern.jarrace.controller.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ExpressionException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Class responsible of writing the deployment file to the file system
 * @author tiagomr
 */
public class JarWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarWriter.class);
    private static final String FILE_EXTENSION = "jar";

    @Value("${io.deploymentdir}")
    private String deploymentPath;

    private Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    @PostConstruct
    void init() throws InvalidPropertyException {
        if(deploymentPath == null || deploymentPath.isEmpty()) {
            throw new InvalidPropertyException(JarWriter.class, "deployment_dir", "Property cannot be null nor empty");
        }
        File deploymentDir = new File(deploymentPath);
        if(!deploymentDir.exists()) {
            if(!deploymentDir.mkdirs()) {
                throw new IllegalStateException(String.format("Deployment folder cannot be created: [%s]",
                        deploymentPath));
            }
        } else if(!deploymentDir.isDirectory()) {
            throw new InvalidPropertyException(JarWriter.class, "deployment_dir", "Property points to a file and not a directory.");
        }
    }

    public String writeFile(String name, byte[] jar) throws IOException, IllegalAccessException {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name of the deployment cannot be null nor empty");
        }

        if(!pattern.matcher(name).matches()) {
            throw new IllegalArgumentException("THe name can be composed only by alphabetic characteres");
        }

        if(jar == null || jar.length == 0) {
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

    void setDeploymentPath(String deploymentPath) {
        this.deploymentPath = deploymentPath;
    }
}