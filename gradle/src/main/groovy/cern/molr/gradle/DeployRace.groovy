/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path
import java.nio.file.Paths

/**
 * A plugin that defines two tasks:
 * <ol>
 *     <li><code>fatJar</code> which packages and stores a jar file
 * containing all the classes in the classpath and the source code of the project running the task.</li>
 *     <li><code>deployJar</code> which sends the packaged jar from the <code>fatJar</code> task to a
 *     server specified in the <code>jarrace.host</code> property. The jar will be registered under
 *     the name specified in the <code>jarrace.name</code> (defaulting to the name of the
 *     project.</li>
 * </ol>
 */
class DeployRace implements Plugin<Project> {

    public static final String TASKS_GROUP = "MolR"
    public static final String HTTP_CONTENT_TYPE_HEADER = "Content-Type"
    public static final String HTTP_POST_METHOD = "POST"
    public static final String SERVER_CONTENT_TYPE = "application/octet-stream"
    public static final String SERVER_DEPLOY_URL = "/molr/container/deploy/"
    public static final String SERVER_PROTOCOL = "http://"
    public static final String MOLR_JAR_SUFFIX = "-molr"

    private static final Logger LOGGER = LoggerFactory.getLogger(DeployRace)

    void apply(Project project) {
        project.extensions.create("molr", MolRExtension)

        def jarName = project.name + MOLR_JAR_SUFFIX

        def createAgentTask = project.task('createMole', type: Jar) {
            description = "Creates a deployable jar with all the classes in the classpath and the source code of the " +
                    "dependent project"

            baseName = jarName

            with project.jar

            // Include all compile dependencies (fatjar)
            from project.configurations.compile.collect { it.isDirectory() ? it : project.zipTree(it) }

            // Include all source code for this project
            from project.sourceSets.main.allSource
            exclude 'META-INF/*.RSA', 'META-INF/*.SF','META-INF/*.DSA'
        }
        createAgentTask.setGroup(TASKS_GROUP)

        def deployTask = project.task('deployMole').dependsOn('createMole') << {
            description = "Sends jar file to a REST endpoint specified in molr.host"
            verifySettings(project)

            def url = SERVER_PROTOCOL + project.molr.host + SERVER_DEPLOY_URL + project.molr.name
            def jarPath = project.jar.destinationDir.toString() + File.separator + jarName + ".jar"
            LOGGER.info("Sending $jarPath to deploy server at $url")

            def connection = openConnection(url)
            writeJarToConnection(jarPath, connection)

            def response = connection.getResponseCode()
            if (response == 200) {
                LOGGER.info("Successfully uploaded mole")
            } else {
                LOGGER.error("Server returned error code $response")
                throw new GradleException("Failed to upload mole to $url. Response code was $response")
            }
        }
        deployTask.setGroup(TASKS_GROUP)
    }

    static HttpURLConnection openConnection(String urlString) {
        URL url = new URL(urlString)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection()
        connection.setRequestMethod(HTTP_POST_METHOD)
        connection.setRequestProperty(HTTP_CONTENT_TYPE_HEADER, SERVER_CONTENT_TYPE)
        connection.setDoOutput(true)
        connection
    }

    static void verifySettings(Project project) {
        if (project.molr.host == null) {
            throw new GradleException("'host' was not defined for the molr plugin extension. Please define it in" +
                    "your build.gradle file with the following syntax:\nmolr {\n\thost = \"host:port\"\n}")
        }

        if (project.molr.name == null) {
            project.molr.name = project.name
        }
    }

    static void writeJarToConnection(String jarPath, HttpURLConnection connection) {
        Path path = Paths.get(jarPath)
        connection.getOutputStream().withCloseable { output ->
            writePathToOutput(path, output)
        }
    }

    static void writePathToOutput(Path path, OutputStream output) {
        path.withInputStream { input ->
            int bufferSize = 4096
            byte[] buffer = new byte[bufferSize]
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead)
            }
        }
    }

}