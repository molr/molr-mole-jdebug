/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.rest.controller;

import cern.jarrace.controller.domain.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Class that tests the behaviour of the {@link AgentContainerController}
 * @author tiagomr
 */

public class AgentContainerControllerTest {

    private MockMvc mockMvc;
    private AgentContainerController agentContainerController;

    @Before
    public void setUp() throws Exception {
        agentContainerController = new AgentContainerController();
        mockMvc = MockMvcBuilders.standaloneSetup(agentContainerController).build();
    }

    @Test
    public void testDeployWithDifferentRequestTypes() throws Exception {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            MockHttpServletRequestBuilder request = request(httpMethod, "/jarrace/container/deploy/SampleDeploy")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .content("SampleBytes".getBytes());
            ResultActions perform = mockMvc.perform(request);

            if (HttpMethod.POST.equals(httpMethod) || HttpMethod.OPTIONS.equals(httpMethod) || HttpMethod.TRACE.equals(httpMethod)) {
                perform.andExpect(status().isOk());
            } else {
                perform.andExpect(status().isMethodNotAllowed());
            }
        }
    }

    /*@Test
    public void testDeploy() throws Exception {
        mockMvc.perform(post("/jarrace/container/deploy/SampleDeploy")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .content("SampleBytes".getBytes()));
        Assert.assertEquals(1, agentContainerController.paths.size());
        File deployFile = new File(agentContainerController.paths.get("SampleDeploy"));
        Assert.assertTrue(deployFile.exists());
        Assert.assertTrue(deployFile.isFile());
        byte[] bytes = Files.readAllBytes(Paths.get(deployFile.getAbsolutePath()));
        Assert.assertThat("SampleBytes".getBytes(), equalTo(bytes));
    }

    @Test
    public void testRegisterServiceWithDifferentRequestTypes() throws Exception {
        agentContainerController.entryPoints.put("SampleContainer", new ArrayList<>());
        for (HttpMethod httpMethod : HttpMethod.values()) {
            MockHttpServletRequestBuilder request = request(httpMethod, "/jarrace/SampleContainer/service/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content("{\"agentName\":\"SampleAgent\",\"clazz\":\"cern.something.SampleClass\",\"entryPoints\":[\"Entrypoint1\",\"Entrypoint2\"]}");
            ResultActions perform = mockMvc.perform(request);

            if (HttpMethod.POST.equals(httpMethod) || HttpMethod.OPTIONS.equals(httpMethod) || HttpMethod.TRACE.equals(httpMethod)) {
                perform.andExpect(status().isOk());
            } else {
                perform.andExpect(status().isMethodNotAllowed());
            }
        }
    }

    @Test
    public void testRegisterServiceWrongMediaType() throws Exception {
        agentContainerController.entryPoints.put("SampleContainer", new ArrayList<>());
        MockHttpServletRequestBuilder request = post("/jarrace/SampleContainer/service/register")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .content("{\"agentName\":\"SampleAgent\",\"clazz\":\"cern.something.SampleClass\",\"entryPoints\":[\"Entrypoint1\",\"Entrypoint2\"]}");
        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void testRegisterServiceWithoutEntrypoints() throws Throwable {
        try {
            agentContainerController.entryPoints.put("SampleContainer", new ArrayList<>());
            MockHttpServletRequestBuilder request = post("/jarrace/SampleContainer/service/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content("{\"agentName\":\"SampleAgent\",\"clazz\":\"cern.something.SampleClass\"}");
            mockMvc.perform(request)
                    .andExpect(status().isInternalServerError());
        } catch (NestedServletException exception) {
            Assert.assertEquals(exception.getRootCause().getClass(), IllegalArgumentException.class);
        }
    }

    @Test
    public void testRegisterServiceWithoutClass() throws Throwable {
        try {
            agentContainerController.entryPoints.put("SampleContainer", new ArrayList<>());
            MockHttpServletRequestBuilder request = post("/jarrace/SampleContainer/service/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content("{\"agentName\":\"SampleAgent\",\"entryPoints\":[\"Entrypoint1\",\"Entrypoint2\"]}");
            mockMvc.perform(request)
                    .andExpect(status().isInternalServerError());
        } catch (NestedServletException exception) {
            Assert.assertEquals(exception.getRootCause().getClass(), IllegalArgumentException.class);
        }
    }

    @Test
    public void testRegisterServiceWithoutAgentName() throws Throwable {
        try {
            agentContainerController.entryPoints.put("SampleContainer", new ArrayList<>());
            MockHttpServletRequestBuilder request = post("/jarrace/SampleContainer/service/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content("{\"clazz\":\"cern.something.SampleClass\",\"entryPoints\":[\"Entrypoint1\",\"Entrypoint2\"]}");
            mockMvc.perform(request)
                    .andExpect(status().isInternalServerError());
        } catch (NestedServletException exception) {
            Assert.assertEquals(exception.getRootCause().getClass(), IllegalArgumentException.class);
        }
    }

    @Test
    public void testRegisterServiceWithNonExistentContainerName() throws Throwable {
        try {
            MockHttpServletRequestBuilder request = post("/jarrace/SampleContainer/service/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content("{\"clazz\":\"cern.something.SampleClass\",\"entryPoints\":[\"Entrypoint1\",\"Entrypoint2\"]}");
            mockMvc.perform(request)
                    .andExpect(status().isInternalServerError());
        } catch (NestedServletException exception) {
            Assert.assertEquals(exception.getRootCause().getClass(), IllegalArgumentException.class);
        }
    }

    @Test
    public void testListContainersWithDifferentRequestTypes() throws Exception {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            ResultActions perform = mockMvc.perform(request(httpMethod, "/jarrace/container/list"));
            if (HttpMethod.GET.equals(httpMethod) || HttpMethod.OPTIONS.equals(httpMethod) || HttpMethod.TRACE.equals(httpMethod)) {
                perform.andExpect(status().isOk());
            } else {
                perform.andExpect(status().isMethodNotAllowed());
            }
        }
    }

    @Test
    public void testListContainersWithNoContainers() throws Exception {
        mockMvc.perform(get("/jarrace/container/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testListContainersWithMultipleContainers() throws Exception {
        for (int i = 1; i <= 3; ++i) {
            agentContainerController.entryPoints.put("SampleContainer" + i, new ArrayList<>());
            mockMvc.perform(get("/jarrace/container/list"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$", hasSize(i)));
        }
    }

    @Test
    public void testListServicesWithNonexistentOrEmptyContainer() throws Exception {
        agentContainerController.entryPoints.put("SampleContainer", new ArrayList<>());
        mockMvc.perform(get("/jarrace/NonexistantContainer/service/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/jarrace/SampleContainer/service/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testListServicesWithOneOrMoreNonEmptyContainer() throws Exception {
        agentContainerController.entryPoints.put("SampleContainer",
                Collections.singletonList(new Service("Agent1", "Class1", Collections.singletonList("Entrypoint1"))));
        mockMvc.perform(get("/jarrace/SampleContainer/service/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        agentContainerController.entryPoints.put("SampleContainer1",
                Arrays.asList(new Service("Service1", "Path1"), new Service("Agent1", "Class1", Collections.singletonList("Entrypoint1"))));
        mockMvc.perform(get("/jarrace/SampleContainer1/service/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }*/
}