/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.io;

import cern.molr.commons.domain.Mole;
import cern.molr.controller.io.JarReader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JarReaderTest {

    public static final String JAVA_HOME = System.getProperty("java.home");
    public static final String MANIFEST_ENTRY = "META-INF/MANIFEST.MF";
    public static final String RT_PATH = JAVA_HOME + File.separator + "lib" + File.separator + "rt.jar";

    private Mole mockedContainer;

    @Before
    public void setup() {
        mockedContainer = mock(Mole.class);
        when(mockedContainer.getContainerPath()).thenReturn(RT_PATH);
    }

    @Test
    public void canCreateReaderFromAgentContainer() throws Exception {
        assertNotNull(JarReader.ofContainer(mockedContainer, Function.identity()));
    }

    @Test(expected = IOException.class)
    public void canFailWhenJarDoesNotExist() throws Exception {
        File tmpFile = File.createTempFile("test", null);
        boolean delete = tmpFile.delete();
        if (!delete) {
            throw new IllegalStateException("Failed to delete temporary file " + tmpFile);
        }
        when(mockedContainer.getContainerPath()).thenReturn(tmpFile.toString());
        JarReader.ofContainer(mockedContainer, Function.identity());
    }

    @Test
    public void canReadEntryInJar() throws IOException {
        assertNotNull(JarReader.ofContainer(mockedContainer, reader -> {
            try {
                return reader.readEntry(MANIFEST_ENTRY);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }));
    }

    @Test(expected = NoSuchElementException.class)
    public void canFailToReadEntryWhenItDoesNotExist() throws IOException {
        JarReader.ofContainer(mockedContainer, reader -> {
            try {
                return reader.readEntry("IDoNotExist");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}