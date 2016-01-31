/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.manager.impl;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.controller.MockUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Class that test the {@link InMemoryAgentContainerManager} features
 * @author tiagomr
 */
public class InMemoryAgentContainerManagerTest {

    private static final String NON_EXISTENT_CONTAINER_NAME = "NON_EXISTENT_CONTAINER_NAME";
    private final InMemoryAgentContainerManager containerManager = new InMemoryAgentContainerManager();

    @Test(expected = IllegalArgumentException.class)
    public void testFindAgentContainerWithNull() {
        containerManager.getAgentContainers().add(MockUtils.getMockedContainer(1, 1, 1));
        containerManager.findAgentContainer(null);
    }

    @Test
    public void testFindAgentContainerWithNonExistentContainerName() {
        containerManager.getAgentContainers().add(MockUtils.getMockedContainer(1, 1, 1));
        assertEquals(null, containerManager.findAgentContainer(NON_EXISTENT_CONTAINER_NAME));
    }

    @Test
    public void testFindAgentContainerWithExistentContainerName() {
        AgentContainer mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.getAgentContainers().add(mockedContainer);
        assertEquals(mockedContainer, containerManager.findAgentContainer(mockedContainer.getContainerName()));
    }

    @Test
    public void testfindAllAgentContainersWithNoContainers() {
        assertEquals(0, containerManager.findAllAgentContainers().size());
    }

    @Test
    public void testfindAllAgentContainersWithOneOrMoreContainers() {
        Queue<AgentContainer> containerQueue = new LinkedList<>(MockUtils.getMockedContainers(5, 1, 1));
        for(int index = 1; index < 6; ++index) {
            AgentContainer polledContainer = containerQueue.poll();
            containerManager.getAgentContainers().add(polledContainer);
            Set<AgentContainer> allAgentContainers = containerManager.findAllAgentContainers();
            assertEquals(index, allAgentContainers.size());
            assertTrue(allAgentContainers.contains(polledContainer));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullAgentContainer() {
        containerManager.registerAgentContainer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAgentContainerWithNullName() {
        AgentContainer agentContainer = new AgentContainer(null, "TestPath", new ArrayList<>());
        containerManager.registerAgentContainer(agentContainer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAgentContainerWithEmptyName() {
        AgentContainer agentContainer = new AgentContainer("", "TestPath", new ArrayList<>());
        containerManager.registerAgentContainer(agentContainer);
    }

    @Test
    public void testRegisterAgentContainer() {
        AgentContainer mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.registerAgentContainer(mockedContainer);
        assertEquals(1, containerManager.getAgentContainers().size());
        assertTrue(containerManager.getAgentContainers().contains(mockedContainer));
    }

    @Test
    public void testRegisterRepeatedAgentContainer() {
        AgentContainer mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.registerAgentContainer(mockedContainer);
        containerManager.registerAgentContainer(mockedContainer);
        assertEquals(1, containerManager.getAgentContainers().size());
        assertTrue(containerManager.getAgentContainers().contains(mockedContainer));
    }
}