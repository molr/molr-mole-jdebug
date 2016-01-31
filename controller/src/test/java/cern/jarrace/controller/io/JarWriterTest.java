package cern.jarrace.controller.io;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

/**
 * Class that test the {@link JarWriter} features
 * @author tiagomr
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IOConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JarWriterTest {

    private static final String TEST_NAME = "TESTNAME";
    private static final String INVALID_NAME = "INVALID_NAME";
    private static final byte[] TEST_BYTES = "TestBytes".getBytes();
    private File tempDirectory;

    @Autowired
    private JarWriter jarWriter;

    @Before
    public void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("tempDirectory").toFile();
        jarWriter.setDeploymentPath(tempDirectory.getAbsolutePath());
    }

    @Test(expected = InvalidPropertyException.class)
    public void testInitWithNullDeploymentPath() {
        jarWriter.setDeploymentPath(null);
        jarWriter.init();
    }

    @Test(expected = InvalidPropertyException.class)
    public void testInitWithEmptyDeploymentPath() {
        jarWriter.setDeploymentPath("");
        jarWriter.init();
    }

    @Test(expected = IllegalStateException.class)
    public void testInitWithUnreachableDeploymentPath() {
        jarWriter.setDeploymentPath("/Some/Unexixtent/Path");
        jarWriter.init();
    }

    @Test(expected = InvalidPropertyException.class)
    public void testInitWithFileDeploymentPath() throws IOException {
        File tempFile = File.createTempFile("temp", "file");
        jarWriter.setDeploymentPath(tempFile.getAbsolutePath());
        jarWriter.init();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteFileWithNullName() throws IOException, IllegalAccessException {
        jarWriter.writeFile(null, TEST_BYTES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteFileWithEmptyName() throws IOException, IllegalAccessException {
        jarWriter.writeFile("", TEST_BYTES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteFileWithNullBytes() throws IOException, IllegalAccessException {
        jarWriter.writeFile(TEST_NAME, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteFileWithEmptyBytes() throws IOException, IllegalAccessException {
        jarWriter.writeFile(TEST_NAME, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteFileWithInvalidName() throws IOException, IllegalAccessException {
        jarWriter.writeFile(INVALID_NAME, TEST_BYTES);
    }

    @Test
    public void testWriteFile() throws IOException, IllegalAccessException {
        jarWriter.writeFile(TEST_NAME, TEST_BYTES);
        File deployedFile = tempDirectory.toPath().resolve(TEST_NAME + ".jar").toFile();
        assertTrue(deployedFile.exists());
        assertTrue(deployedFile.isFile());
    }
}