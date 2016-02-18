/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.commons.domain.Service;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class that provides utility methods for mocking domain objects
 *
 * @author tiagomr
 */
public class MockUtils {
    public static List<MoleContainer> getMockedContainers(int numberOfAgentContainers, int numberOfServices, int numberOfEntryPoints) {
        List<MoleContainer> mockedMoleContainers = new ArrayList<>();
        for (int index = 0; index < numberOfAgentContainers; ++index) {
            mockedMoleContainers.add(getMockedContainer(index, numberOfServices, numberOfEntryPoints));
        }
        return mockedMoleContainers;
    }

    public static MoleContainer getMockedContainer(int containerNumber, int numberOfServices, int numberOfEntryPoints) {
        MoleContainer mockedMoleContainer = mock(MoleContainer.class);
        when(mockedMoleContainer.getContainerName()).thenReturn("MockedContainerName" + containerNumber);
        when(mockedMoleContainer.getContainerPath()).thenReturn("MockedContainerPath" + containerNumber);
        List<Service> mockedService = getMockedService(numberOfServices, numberOfEntryPoints);
        when(mockedMoleContainer.getServices()).thenReturn(mockedService);
        return mockedMoleContainer;
    }

    public static List<Service> getMockedService(int numberOfServices, int numberOfEntryPoints) {
        List<Service> mockedServices = new ArrayList<>();
        for (int serviceIndex = 0; serviceIndex < numberOfServices; ++serviceIndex) {
            List<String> entryPoints = new ArrayList<>();
            for (int index = 0; index < numberOfEntryPoints; ++index) {
                entryPoints.add("MockedEntryPoint" + index);
            }
            Service mockedService = mock(Service.class);
            when(mockedService.getMoleClassName()).thenReturn("MockedAgentName");
            when(mockedService.getServiceClassName()).thenReturn("MockedClassName");
            when(mockedService.getTasksNames()).thenReturn(entryPoints);
            mockedServices.add(mockedService);
        }
        return mockedServices;
    }

}
