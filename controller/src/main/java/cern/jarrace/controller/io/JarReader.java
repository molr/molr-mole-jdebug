/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.controller.io;

import cern.jarrace.commons.domain.AgentContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Reads entries as strings from inside a jar file.
 */
public class JarReader implements AutoCloseable {

    private final JarFile jarFile;

    /**
     * Creates a JarReader from a given {@link JarFile}.
     *
     * @param jarFile The jar to read from.
     */
    JarReader(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public void close() throws IOException {
        jarFile.close();
    }

    /**
     * Reads the content of the given entry file in the jar of this JarReader, if it exists.
     *
     * @param entryName The name of the entry inside the jar file. Example: <code>cern.test.Class.java</code>
     * @return A String with the contents of the file.
     * @throws IOException If the entry could not be read.
     */
    public String readEntry(String entryName) throws IOException {
        ZipEntry entry = jarFile.getEntry(entryName);
        if (entry == null) {
            throw new NoSuchElementException(String.format("Entry '%s' could not be found in jar %s", entryName, jarFile));
        }
        try (InputStream input = jarFile.getInputStream(entry);
             InputStreamReader inputReader = new InputStreamReader(input);
             BufferedReader reader = new BufferedReader(inputReader)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Creates a {@link JarReader} and executes a function on the reader. The {@link AgentContainer#getContainerPath()}
     * is used to locate the jar file.
     *
     * @param container The container with a path to a jar file.
     * @param function  The function to run on the JarReader.
     * @param <R>       The return type of the function.
     * @return A new instance of a reader.
     * @throws IOException If the jar file could not be read.
     */
    public static <R> R ofContainer(AgentContainer container, Function<JarReader, R> function) throws IOException {
        Objects.requireNonNull(container, "Container may not be null");
        final JarFile jarFile = new JarFile(container.getContainerPath());
        try (JarReader jarReader = new JarReader(jarFile)) {
            return function.apply(jarReader);
        }
    }

}
