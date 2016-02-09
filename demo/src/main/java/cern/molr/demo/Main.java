package cern.molr.demo;


import cern.jarrace.commons.domain.Service;
import cern.jarrace.commons.instantiation.InstantiationRequest;
import cern.jarrace.commons.instantiation.JsonInstantiationRequest;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        final String molrJarPath = "/home/jens/workspace/molr/demo/build/libs/demo-jarrace.jar";
        final String localClassPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        final String classPath = molrJarPath + File.pathSeparator + localClassPath;
        final Service entryPoint = new Service("cern.jarrace.agent.impl.JunitAgent", "cern.jarrace.gradle.SimpleTest");

        System.out.println(classPath);

        JsonInstantiationRequest request = new JsonInstantiationRequest(classPath, entryPoint);
        ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/java", "-cp", classPath, "cern.jarrace.inspector.CliMain", request.toJson())
                .inheritIO();
        System.out.println(processBuilder.command());
        Process process = processBuilder.start();
        Thread.sleep(1000);
        process.destroy();
    }

}
