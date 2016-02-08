/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector;

import cern.jarrace.commons.domain.Service;
import cern.jarrace.commons.instantiation.JsonInstantiationRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CliMainTest {

    private Service mockedService;

    @Before
    public void setup() {
        mockedService = mock(Service.class);
    }

    @Test
    public void canStartAnInspector() throws Exception {
        final String classPath = "/home/jepeders/workspace/cern/molr/inspector/build/main/";
        final String inspectionClass = "cern.jarrace.inspector.CliMain";
        final String mainClass = "cern.jarrace.inspector.CliMain";
        final List<String> entryPoints = Collections.singletonList("main");
        when(mockedService.getAgentName()).thenReturn(mainClass);
        when(mockedService.getClassName()).thenReturn(inspectionClass);
        when(mockedService.getEntryPoints()).thenReturn(entryPoints);
        final JsonInstantiationRequest request = new JsonInstantiationRequest(classPath, mockedService);
        CliMain.main(new String[]{request.toJson()});
    }

}
