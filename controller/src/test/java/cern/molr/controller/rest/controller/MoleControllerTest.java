/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.rest.controller;

import cern.molr.commons.domain.Mole;
import cern.molr.commons.domain.Service;
import cern.molr.controller.rest.controller.AgentContainerController;
import cern.molr.controller.server.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Class that tests the behaviour of the {@link AgentContainerController}
 *
 * @author tiagomr
 */

@RunWith(MockitoJUnitRunner.class)
public class MoleControllerTest {

    private static final String JARRACE_CONTAINER_LIST_PATH = "/jarrace/container/list";
    private static final String JARRACE_CONTAINER_REGISTER_PATH = "/jarrace/container/register";
    private static final String TEST_CONTAINER_NAME = "TEST_CONTAINER_NAME";
    private static final String JARRACE_CONTAINER_TEST_NAME_START_PATH = "/jarrace/container/" + TEST_CONTAINER_NAME + "/start";
    private static final String JARRACE_CONTAINER_TEST_NAME_READ_PATH = "/jarrace/container/" + TEST_CONTAINER_NAME + "/read";

    private static final String TEST_SERVICE_NAME = "TEST_SERVICE_NAME";
    private static final String TEST_ENTRY_POINT_1 = "TEST_ENTRY_POINT_1";
    private static final String TEST_ENTRY_POINT_2 = "TEST_ENTRY_POINT_2";
    private static final String TEST_AGENT_NAME = "TEST_AGENT_NAME";
    private static final String TEST_NAME1 = "TEST_NAME";
    private static final String TEST_PATH = "TEST_PATH";

    private static final String SERVICE_PARAM_NAME = "service";
    private static final String ENTRY_POINTS_PARAM_NAME = "entryPoints";
    private static final String CLASS_ENTRY_NAME = "class";
    private static final String JAVA_CLASS_SUFFIX = ".java";
    private static final byte[] TEST_CONTAINER_BYTES = "TEST_CONTAINER_BYTES".getBytes();
    private static final String TEST_EXECUTION_RESULT = "TEST_EXECUTION_RESULT";

    private MockMvc mockMvc;
    @InjectMocks
    private AgentContainerController agentContainerController;
    @Mock
    private Controller controller;
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(agentContainerController)
                .addPlaceHolderValue("rest.basepath", "/jarrace")
                .build();
    }

    @Test
    public void testDeploy() throws Exception {
        mockMvc.perform(post("/jarrace/container/deploy/" + TEST_CONTAINER_NAME)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .content(TEST_CONTAINER_BYTES))
                .andExpect(status().isOk());
        verify(controller).deploy(TEST_CONTAINER_NAME, TEST_CONTAINER_BYTES);
    }

    @Test
    public void testRegister() throws Exception {
        Mole testMole = getTestAgentContainer();
        mockMvc.perform(post(JARRACE_CONTAINER_REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJson(testMole)))
                .andExpect(status().isOk());
        verify(controller).registerService(testMole);
    }

    @Test
    public void testRegisterWithWrongMediaType() throws Exception {
        mockMvc.perform(post(JARRACE_CONTAINER_REGISTER_PATH)
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void testRegisterWithWrongMethodType() throws Exception {
        mockMvc.perform(get(JARRACE_CONTAINER_REGISTER_PATH))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testRegisterWithNoContent() throws Exception {
        mockMvc.perform(post(JARRACE_CONTAINER_REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testListWithAgentContainers() throws Exception {
        Set<Mole> containers = Collections.singleton(getTestAgentContainer());
        when(controller.getAllContainers()).thenReturn(containers);
        mockMvc.perform(get(JARRACE_CONTAINER_LIST_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(getJson(containers)));
        verify(controller).getAllContainers();
    }

    @Test
    public void testListWithNoAgentContainers() throws Exception {
        Set<Mole> containers = Collections.emptySet();
        when(controller.getAllContainers()).thenReturn(containers);
        mockMvc.perform(get(JARRACE_CONTAINER_LIST_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(getJson(containers)));
        verify(controller).getAllContainers();
    }

    @Test
    public void testListWithWrongRequestType() throws Exception {
        mockMvc.perform(post(JARRACE_CONTAINER_LIST_PATH))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testStartWithWrongRequestType() throws Exception {
        mockMvc.perform(post(JARRACE_CONTAINER_TEST_NAME_START_PATH))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testStartWithNoParameters() throws Exception {
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH))
                .andExpect(status().isBadRequest());
    }
/*
    @Test
    public void testStartWithNonExistentContainerName() throws Exception {
        Mole agentContainer = mock(Mole.class);
        when(agentContainer.getContainerPath()).thenReturn(TEST_PATH);
        when(controller.getContainer(TEST_CONTAINER_NAME)).thenReturn(Optional.of(agentContainer));
        when(controller.runMole(anyString(), any(Service.class), anyList())).thenReturn(TEST_EXECUTION_RESULT);
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME))
                .andExpect(content().string(TEST_EXECUTION_RESULT));
        verify(controller).getContainer(TEST_CONTAINER_NAME);
        verify(controller).runMole(anyString(), any(Service.class), anyList());
    }

    @Test
    public void testStartWithNoNonExistentService() throws Exception {
        expectedException.expect(NestedServletException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        Mole container = getTestAgentContainer();
        when(agentContainerManager.getMole(TEST_NAME)).thenReturn(Optional.of(container));
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME));
    }

    @Test
    public void testStartWithNonExistentEntrypoint() throws Exception {
        expectedException.expect(NestedServletException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        Mole container = getTestAgentContainer();
        when(agentContainerManager.getMole(TEST_NAME)).thenReturn(Optional.of(container));
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME)
                .param(ENTRY_POINTS_PARAM_NAME, TEST_ENTRY_POINT_1 + "_NON_EXISTENT"));
    }

    @Test
    public void testStart() throws Exception {
        Mole container = getTestAgentContainer();
        when(agentContainerManager.getMole(TEST_NAME)).thenReturn(Optional.of(container));
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME)
                .param(ENTRY_POINTS_PARAM_NAME, TEST_ENTRY_POINT_1, TEST_ENTRY_POINT_2))
                .andExpect(status().isOk());
        verify(agentRunnerSpawner).spawnAgentRunner(Matchers.any(Service.class), anyString(), anyList());
    }

    @Test
    public void fetchesEntryInsideJar() throws Exception {
        final String content = "testContent\nSpanning\nLines";
        final File jarFile = setupJarContainer(content, TEST_PATH);
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_READ_PATH)
                .param(CLASS_ENTRY_NAME, TEST_PATH))
                .andExpect(content().string(content));
        //noinspection ResultOfMethodCallIgnored
        jarFile.delete();
    }

    @Test
    public void failsToFetchEntryFromJarWhenContainerDoesNotExist() throws Exception {
        when(agentContainerManager.getMole(TEST_NAME)).thenReturn(Optional.empty());
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_READ_PATH)
                .param(CLASS_ENTRY_NAME, TEST_NAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failsToFetchEntryFromJarWhenEntryDoesNotExist() throws Exception {
        final String content = "TestContent";
        final File jarFile = setupJarContainer(content, TEST_PATH + AgentContainerController.JAVA_CLASS_SUFFIX);
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_READ_PATH)
                .param(CLASS_ENTRY_NAME, "IDoNotExist"))
                .andExpect(status().isBadRequest());
    }*/

    private Mole getTestAgentContainer() {
        List<String> entryPoints = new ArrayList<>();
        entryPoints.add(TEST_ENTRY_POINT_1);
        entryPoints.add(TEST_ENTRY_POINT_2);
        List<Service> services = new ArrayList<>();
        services.add(new Service(TEST_AGENT_NAME, TEST_SERVICE_NAME, entryPoints));
        return new Mole(TEST_NAME1, TEST_PATH, services);
    }

    private String getJson(Object object) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer();
        return writer.writeValueAsString(object);
    }

    /*private File setupJarContainer(String content, String entry) throws IOException {
        final File jarFile = writeToZip(entry + JAVA_CLASS_SUFFIX, content);
        final Mole mockedContainer = mock(Mole.class);
        when(agentContainerManager.getMole(TEST_NAME)).thenReturn(Optional.of(mockedContainer));
        when(mockedContainer.getContainerPath()).thenReturn(jarFile.toString());
        return jarFile;
    }*/

    private File writeToZip(String entry, String data) throws IOException {
        File tmpFile = File.createTempFile("test", null);
        try (FileOutputStream fileOutput = new FileOutputStream(tmpFile);
             ZipOutputStream zipOutput = new ZipOutputStream(fileOutput)) {
            ZipEntry zipEntry = new JarEntry(entry);
            zipOutput.putNextEntry(zipEntry);
            zipOutput.write(data.getBytes());
        }
        return tmpFile;
    }
}