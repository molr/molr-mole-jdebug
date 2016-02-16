/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector;

import cern.molr.commons.domain.Service;
import cern.molr.inspector.remote.SystemMain;
import cern.molr.inspector.domain.InstantiationRequest;
import cern.molr.inspector.domain.InstantiationRequestImpl;
import cern.molr.inspector.json.ServiceTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SystemMainTest {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Service.class, new ServiceTypeAdapter().nullSafe())
            .create();

    private Service mockedService;

    @Before
    public void setup() {
        mockedService = mock(Service.class);
    }

    @Test
    public void canStartAnInspector() throws Exception {
        final String classPath = "/home/jepeders/workspace/cern/molr/inspector/build/main/";
        final String inspectionClass = "cern.jarrace.inspector.remote.CliMain";
        final String mainClass = "cern.jarrace.inspector.remote.CliMain";
        final List<String> entryPoints = Collections.singletonList("main");
        when(mockedService.getMoleClassName()).thenReturn(mainClass);
        when(mockedService.getServiceClassName()).thenReturn(inspectionClass);
        when(mockedService.getEntryPoints()).thenReturn(entryPoints);
        final InstantiationRequest request = new InstantiationRequestImpl(classPath, mockedService);
        SystemMain.main(new String[]{GSON.toJson(request)});
    }

}
