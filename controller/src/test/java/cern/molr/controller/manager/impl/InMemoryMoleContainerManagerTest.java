/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.manager.impl;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.controller.MockUtils;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Class that test the {@link InMemoryMoleManager} features
 *
 * @author tiagomr
 */
public class InMemoryMoleContainerManagerTest {

    private static final String NON_EXISTENT_CONTAINER_NAME = "NON_EXISTENT_CONTAINER_NAME";
    private final InMemoryMoleManager containerManager = new InMemoryMoleManager();

    @Test(expected = IllegalArgumentException.class)
    public void testFindAgentContainerWithNull() {
        containerManager.getMoleContainers().add(MockUtils.getMockedContainer(1, 1, 1));
        containerManager.getMole(null);
    }

    @Test
    public void testFindAgentContainerWithNonExistentContainerName() {
        containerManager.getMoleContainers().add(MockUtils.getMockedContainer(1, 1, 1));
        assertEquals(Optional.empty(), containerManager.getMole(NON_EXISTENT_CONTAINER_NAME));
    }

    @Test
    public void testFindAgentContainerWithExistentContainerName() {
        MoleContainer mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.getMoleContainers().add(mockedContainer);
        assertEquals(Optional.of(mockedContainer), containerManager.getMole(mockedContainer.getContainerName()));
    }

    @Test
    public void testfindAllAgentContainersWithNoContainers() {
        assertEquals(0, containerManager.getAllMoles().size());
    }

    @Test
    public void testfindAllAgentContainersWithOneOrMoreContainers() {
        Queue<MoleContainer> containerQueue = new LinkedList<>(MockUtils.getMockedContainers(5, 1, 1));
        for (int index = 1; index < 6; ++index) {
            MoleContainer polledContainer = containerQueue.poll();
            containerManager.getMoleContainers().add(polledContainer);
            Set<MoleContainer> allMoleContainers = containerManager.getAllMoles();
            assertEquals(index, allMoleContainers.size());
            assertTrue(allMoleContainers.contains(polledContainer));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullAgentContainer() {
        containerManager.registerMole(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAgentContainerWithNullName() {
        MoleContainer moleContainer = new MoleContainer(null, "TestPath", new ArrayList<>());
        containerManager.registerMole(moleContainer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAgentContainerWithEmptyName() {
        MoleContainer moleContainer = new MoleContainer("", "TestPath", new ArrayList<>());
        containerManager.registerMole(moleContainer);
    }

    @Test
    public void testRegisterAgentContainer() {
        MoleContainer mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.registerMole(mockedContainer);
        assertEquals(1, containerManager.getMoleContainers().size());
        assertTrue(containerManager.getMoleContainers().contains(mockedContainer));
    }

    @Test
    public void testRegisterRepeatedAgentContainer() {
        MoleContainer mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.registerMole(mockedContainer);
        containerManager.registerMole(mockedContainer);
        assertEquals(1, containerManager.getMoleContainers().size());
        assertTrue(containerManager.getMoleContainers().contains(mockedContainer));
    }
}