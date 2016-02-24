/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.gradle

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static cern.molr.gradle.DeployRace.MOLR_JAR_SUFFIX
import static cern.molr.gradle.DeployRace.SERVER_DEPLOY_URL
import static org.junit.Assert.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class DeployRaceIntegrationTest {

    private static int SERVER_TEST_PORT = 8080
    private HttpHandler mockedHandler;
    private HttpServer server;
    private Project project;

    @Before
    public void setup() {
        mockedHandler = mock(HttpHandler)
        server = HttpServer.create(new InetSocketAddress(SERVER_TEST_PORT), 0)
        server.createContext("/", mockedHandler)
        server.start()
        project = buildProject()
    }

    @After
    public void teardown() {
        server.stop(0)
        project.rootDir.toPath().deleteDir()
    }

    @Test
    public void canCreateFatJar() {
        def jarFile = getFatJarPath(project)
        project.createMole.execute()
        assertTrue(Files.exists(jarFile))
    }

    @Test
    public void canDeployJarToServer() {
        prepareRequest { HttpExchange exchange ->
            def bytes = exchange.getRequestBody().getBytes()
            def jarBytes = Files.readAllBytes(getFatJarPath(project))
            assertArrayEquals(bytes, jarBytes)
        }

        project.createMole.execute()
        project.deployMole.execute()
    }

    @Test
    public void canDeployToCorrectURL() {
        prepareRequest { HttpExchange exchange ->
            assertEquals(SERVER_DEPLOY_URL + project.name, exchange.requestURI.toString())
        }

        project.createMole.execute()
        project.deployMole.execute()
    }

    @Test(expected = GradleException)
    public void canFailWhenServerReturnsError() {
        prepareRequest { HttpExchange exchange ->
            exchange.sendResponseHeaders(404, 0)
            exchange.close()
        }
        project.createMole.execute()
        project.deployMole.execute()
    }

    private static Project buildProject() {
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'molr'
        project.extensions.configure(MolRExtension, new ClosureBackedAction<MolRExtension>({
            host = "localhost:" + SERVER_TEST_PORT
        }))
        project
    }

    private static Path getFatJarPath(Project project) {
        def jarName = project.name + MOLR_JAR_SUFFIX + ".jar"
        Paths.get(project.jar.destinationDir.toString() + File.separator + jarName)
    }

    private void prepareRequest(Closure<HttpExchange> closure) {
        when(mockedHandler.handle(Matchers.any(HttpExchange))).thenAnswer(getAnswer(closure))
    }

    private static Answer<HttpExchange> getAnswer(Closure<HttpExchange> closure) {
        return new Answer<HttpExchange>() {
            @Override
            HttpExchange answer(InvocationOnMock invocation) throws Throwable {
                def exchange = invocation.getArgumentAt(0, HttpExchange)
                closure(exchange)
                exchange.sendResponseHeaders(200, 0)
                exchange.close()
            }
        }
    }

}
