/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector;

import cern.jarrace.inspector.cli.CliApplication;
import cern.jarrace.inspector.controller.BlockingJdiController;
import cern.jarrace.inspector.controller.JdiController;
import cern.jarrace.inspector.entry.EntryMethod;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import org.jdiscript.util.VMLauncher;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * The main entry point to inspect a Java application.
 */
public class Inspector extends CliApplication implements Closeable {

    private final JdiController controller;
    private final Process process;

    private Inspector(JdiController controller, Process process) {
        super(Collections.emptyList());
        this.controller = controller;
        this.process = process;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void main(String[] args) {
        try {
            Inspector inspector = builder()
                    .setBinaryPath(Paths.get("/opt/jepeders/workspace/lhc-inspector/out/production/inspector/"))
                    .setInspectable("cern.jarrace.inspector.Demo", "test").setMainClass("cern.jarrace.inspector.Demo").build();

            Runtime.getRuntime().addShutdownHook(new Thread(inspector::close));

//            Scanner input = new Scanner(System.in);
//            String line;
//            while ((line = input.next()) != null) {
//                inspector.execute(line, System.out);
//            }
            Thread.sleep(1000);
            inspector.execute("exit", System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            controller.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getStandardOut() {
        return process.getInputStream();
    }

    public InputStream getErrorOut() {
        return process.getErrorStream();
    }

    public void stepOver(ThreadReference thread) {
        try {
            thread.resume();
        } catch (Exception e) {
            close();
        }
    }

    @Override
    public String getHelp() {
        return "Available commands: none";
    }

    public static class Builder {

        private static final String CLASSPATH_PREFIX = "-cp ";

        private String inspectableClassName = null;
        private String inspectableMethodName = null;
        private Path binaryPath = null;
        private String mainClassName = null;

        public Inspector build() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IOException,
                IllegalConnectorArgumentsException, VMStartException {
            Objects.requireNonNull(inspectableClassName, "Class name not set");
            Objects.requireNonNull(inspectableMethodName, "Method name not set");
            Objects.requireNonNull(binaryPath, "Path to binaries not set");

            Class<?> inspectableClass = Class.forName(inspectableClassName);
            EntryMethod method = EntryMethod.ofClassAndMethod(inspectableClass, inspectableMethodName);

            VMLauncher launcher = new VMLauncher(CLASSPATH_PREFIX + binaryPath.toString(), mainClassName);

            BlockingJdiController controller = BlockingJdiController
                    .builder()
                    .setInspectableMethod(method)
                    .setLauncher(launcher)
                    .build();
            return new Inspector(controller, launcher.getProcess());
        }

        /**
         * @param path A {@link Path} to the root of the binaries to be inspected.
         */
        public Builder setBinaryPath(Path path) {
            this.binaryPath = path;
            return this;
        }

        /**
         * @param className The full class name of the main class to instrument. Example: org.example.MainClass
         */
        public Builder setMainClass(String className) {
            this.mainClassName = className;
            return this;
        }

        /**
         * @param className  The full class name of the class to inspect. Example: org.example.ClassName
         * @param methodName The name of the method to inspect.
         */
        public Builder setInspectable(String className, String methodName) {
            this.inspectableClassName = className;
            this.inspectableMethodName = methodName;
            return this;
        }

    }

}
