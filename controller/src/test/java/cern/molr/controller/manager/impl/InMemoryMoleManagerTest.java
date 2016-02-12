/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.manager.impl;

import cern.molr.commons.domain.Mole;
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
public class InMemoryMoleManagerTest {

    private static final String NON_EXISTENT_CONTAINER_NAME = "NON_EXISTENT_CONTAINER_NAME";
    private final InMemoryMoleManager containerManager = new InMemoryMoleManager();

    @Test(expected = IllegalArgumentException.class)
    public void testFindAgentContainerWithNull() {
        containerManager.getMoles().add(MockUtils.getMockedContainer(1, 1, 1));
        containerManager.getMole(null);
    }

    @Test
    public void testFindAgentContainerWithNonExistentContainerName() {
        containerManager.getMoles().add(MockUtils.getMockedContainer(1, 1, 1));
        assertEquals(Optional.empty(), containerManager.getMole(NON_EXISTENT_CONTAINER_NAME));
    }

    @Test
    public void testFindAgentContainerWithExistentContainerName() {
        Mole mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.getMoles().add(mockedContainer);
        assertEquals(Optional.of(mockedContainer), containerManager.getMole(mockedContainer.getContainerName()));
    }

    @Test
    public void testfindAllAgentContainersWithNoContainers() {
        assertEquals(0, containerManager.getAllMoles().size());
    }

    @Test
    public void testfindAllAgentContainersWithOneOrMoreContainers() {
        Queue<Mole> containerQueue = new LinkedList<>(MockUtils.getMockedContainers(5, 1, 1));
        for (int index = 1; index < 6; ++index) {
            Mole polledContainer = containerQueue.poll();
            containerManager.getMoles().add(polledContainer);
            Set<Mole> allMoles = containerManager.getAllMoles();
            assertEquals(index, allMoles.size());
            assertTrue(allMoles.contains(polledContainer));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullAgentContainer() {
        containerManager.registerMole(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAgentContainerWithNullName() {
        Mole mole = new Mole(null, "TestPath", new ArrayList<>());
        containerManager.registerMole(mole);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterAgentContainerWithEmptyName() {
        Mole mole = new Mole("", "TestPath", new ArrayList<>());
        containerManager.registerMole(mole);
    }

    @Test
    public void testRegisterAgentContainer() {
        Mole mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.registerMole(mockedContainer);
        assertEquals(1, containerManager.getMoles().size());
        assertTrue(containerManager.getMoles().contains(mockedContainer));
    }

    @Test
    public void testRegisterRepeatedAgentContainer() {
        Mole mockedContainer = MockUtils.getMockedContainer(1, 1, 1);
        containerManager.registerMole(mockedContainer);
        containerManager.registerMole(mockedContainer);
        assertEquals(1, containerManager.getMoles().size());
        assertTrue(containerManager.getMoles().contains(mockedContainer));
    }
}