/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.io;

import cern.molr.controller.io.IOConfiguration;
import cern.molr.controller.io.JarWriter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Class that test the {@link JarWriter} features
 *
 * @author tiagomr
 */

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IOConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JarWriterTest {

    private static final String TEST_NAME = "TESTNAME";
    private static final String INVALID_NAME = "INVALID_NAME";
    private static final byte[] TEST_BYTES = "TestBytes".getBytes();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();

    @Autowired
    private JarWriter jarWriter;

    @Before
    public void setUp() throws IOException {
        jarWriter.setDeploymentPath(tempDirectory.getRoot().getAbsolutePath());
    }

    @Test
    public void testInitWithNullDeploymentPath() {
        expectedException.expect(InvalidPropertyException.class);
        jarWriter.setDeploymentPath(null);
        jarWriter.init();
    }

    @Test
    public void testInitWithEmptyDeploymentPath() {
        expectedException.expect(InvalidPropertyException.class);
        jarWriter.setDeploymentPath("");
        jarWriter.init();
    }

    @Test
    public void testInitWithUnreachableDeploymentPath() {
        expectedException.expect(IllegalStateException.class);
        jarWriter.setDeploymentPath("/Some/Unexixtent/Path");
        jarWriter.init();
    }

    @Test
    public void testInitWithFileDeploymentPath() throws IOException {
        expectedException.expect(InvalidPropertyException.class);
        File tempFile = File.createTempFile("temp", "file");
        jarWriter.setDeploymentPath(tempFile.getAbsolutePath());
        jarWriter.init();
    }

    @Test
    public void testWriteFileWithNullName() throws IOException, IllegalAccessException {
        expectedException.expect(IllegalArgumentException.class);
        jarWriter.writeFile(null, TEST_BYTES);
    }

    @Test
    public void testWriteFileWithEmptyName() throws IOException, IllegalAccessException {
        expectedException.expect(IllegalArgumentException.class);
        jarWriter.writeFile("", TEST_BYTES);
    }

    @Test
    public void testWriteFileWithNullBytes() throws IOException, IllegalAccessException {
        expectedException.expect(IllegalArgumentException.class);
        jarWriter.writeFile(TEST_NAME, null);
    }

    @Test
    public void testWriteFileWithEmptyBytes() throws IOException, IllegalAccessException {
        expectedException.expect(IllegalArgumentException.class);
        jarWriter.writeFile(TEST_NAME, null);
    }

    @Test
    public void testWriteFileWithInvalidName() throws IOException, IllegalAccessException {
        expectedException.expect(IllegalArgumentException.class);
        jarWriter.writeFile(INVALID_NAME, TEST_BYTES);
    }

    @Test
    public void testWriteFile() throws IOException, IllegalAccessException {
        jarWriter.writeFile(TEST_NAME, TEST_BYTES);
        File deployedFile = tempDirectory.getRoot().toPath().resolve(TEST_NAME + ".jar").toFile();
        assertTrue(deployedFile.exists());
        assertTrue(deployedFile.isFile());
    }
}