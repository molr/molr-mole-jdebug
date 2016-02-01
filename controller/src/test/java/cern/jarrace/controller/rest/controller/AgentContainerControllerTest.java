/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.rest.controller;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.io.JarWriter;
import cern.jarrace.controller.jvm.AgentRegistrySpawner;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import cern.jarrace.controller.manager.AgentContainerManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.*;

import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
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
public class AgentContainerControllerTest {

    private static final String JARRACE_CONTAINER_LIST_PATH = "/jarrace/container/list";
    private static final String JARRACE_CONTAINER_REGISTER_PATH = "/jarrace/container/register";
    private static final String TEST_NAME = "TEST_NAME";
    private static final String JARRACE_CONTAINER_TEST_NAME_START_PATH = "/jarrace/container/" + TEST_NAME + "/start";
    private static final String TEST_SERVICE_NAME = "TEST_SERVICE_NAME";
    private static final String TEST_ENTRY_POINT_1 = "TEST_ENTRY_POINT_1";
    private static final String TEST_ENTRY_POINT_2 = "TEST_ENTRY_POINT_2";
    private static final String TEST_AGENT_NAME = "TEST_AGENT_NAME";
    private static final String TEST_NAME1 = "TEST_NAME";
    private static final String TEST_PATH = "TEST_PATH";
    private static final String SERVICE_PARAM_NAME = "service";
    private static final String ENTRY_POINTS_PARAM_NAME = "entryPoints";
    private MockMvc mockMvc;
    private AgentContainerController agentContainerController;

    @Mock
    private AgentContainerManager agentContainerManager;
    @Mock
    private AgentRegistrySpawner agentRegistrySpawner;
    @Mock
    private AgentRunnerSpawner agentRunnerSpawner;
    @Mock
    private JarWriter jarWriter;
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        agentContainerController = new AgentContainerController();
        agentContainerController.setAgentContainerManager(agentContainerManager);
        agentContainerController.setAgentRegistrySpawner(agentRegistrySpawner);
        agentContainerController.setAgentRunnerSpawner(agentRunnerSpawner);
        agentContainerController.setJarWriter(jarWriter);
        mockMvc = MockMvcBuilders.standaloneSetup(agentContainerController)
                .addPlaceHolderValue("rest.basepath", "/jarrace")
                .build();
    }

    @Test
    public void testDeploy() throws Exception {
        when(jarWriter.writeFile("TestContainer", "TestBytes".getBytes())).thenReturn("MockedPath");
        mockMvc.perform(post("/jarrace/container/deploy/TestContainer")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .content("TestBytes".getBytes()))
                .andExpect(status().isOk());
        verify(jarWriter).writeFile("TestContainer", "TestBytes".getBytes());
        verify(agentRegistrySpawner).spawnAgentRegistry("TestContainer", "MockedPath");
    }

    @Test
    public void testRegister() throws Exception {
        mockMvc.perform(post(JARRACE_CONTAINER_REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJson(getTestAgentContainer())))
                .andExpect(status().isOk());
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
        Set<AgentContainer> containers = Collections.singleton(getTestAgentContainer());
        when(agentContainerManager.findAllAgentContainers()).thenReturn(containers);
        mockMvc.perform(get(JARRACE_CONTAINER_LIST_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(getJson(containers)));
    }

    @Test
    public void testListWithNoAgentContainers() throws Exception {
        Set<AgentContainer> containers = Collections.emptySet();
        when(agentContainerManager.findAllAgentContainers()).thenReturn(containers);
        mockMvc.perform(get(JARRACE_CONTAINER_LIST_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(getJson(containers)));
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

    @Test
    public void testStartWithNonExistentContainerName() throws Exception {
        expectedException.expect(NestedServletException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        when(agentContainerManager.findAgentContainer(TEST_NAME)).thenReturn(null);
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME));
    }

    @Test
    public void testStartWithNoNonExistentService() throws Exception {
        expectedException.expect(NestedServletException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        AgentContainer container = getTestAgentContainer();
        when(agentContainerManager.findAgentContainer(TEST_NAME)).thenReturn(Optional.of(container));
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME));
    }

    @Test
    public void testStartWithNonExistentEntrypoint() throws Exception {
        expectedException.expect(NestedServletException.class);
        expectedException.expectCause(isA(IllegalArgumentException.class));
        AgentContainer container = getTestAgentContainer();
        when(agentContainerManager.findAgentContainer(TEST_NAME)).thenReturn(Optional.of(container));
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME)
                .param(ENTRY_POINTS_PARAM_NAME, TEST_ENTRY_POINT_1 + "_NON_EXISTENT"));
    }

    @Test
    public void testStart() throws Exception {
        AgentContainer container = getTestAgentContainer();
        when(agentContainerManager.findAgentContainer(TEST_NAME)).thenReturn(Optional.of(container));
        mockMvc.perform(get(JARRACE_CONTAINER_TEST_NAME_START_PATH)
                .param(SERVICE_PARAM_NAME, TEST_SERVICE_NAME)
                .param(ENTRY_POINTS_PARAM_NAME, TEST_ENTRY_POINT_1, TEST_ENTRY_POINT_2))
                .andExpect(status().isOk());
        verify(agentRunnerSpawner).spawnAgentRunner(Matchers.any(Service.class), anyString(), anyList());
    }

    private AgentContainer getTestAgentContainer() {
        List<String> entryPoints = new ArrayList<>();
        entryPoints.add(TEST_ENTRY_POINT_1);
        entryPoints.add(TEST_ENTRY_POINT_2);
        List<Service> services = new ArrayList<>();
        services.add(new Service(TEST_AGENT_NAME, TEST_SERVICE_NAME, entryPoints));
        return new AgentContainer(TEST_NAME1, TEST_PATH, services);
    }

    private String getJson(Object object) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer();
        return writer.writeValueAsString(object);
    }
}