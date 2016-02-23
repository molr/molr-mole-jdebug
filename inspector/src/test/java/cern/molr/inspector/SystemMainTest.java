/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector;

import cern.molr.commons.domain.Mission;
import cern.molr.inspector.domain.InstantiationRequest;
import cern.molr.inspector.domain.impl.InstantiationRequestImpl;
import cern.molr.inspector.json.MissionTypeAdapter;
import cern.molr.inspector.remote.SystemMain;
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
            .registerTypeAdapter(Mission.class, new MissionTypeAdapter().nullSafe())
            .create();

    private Mission mockedMission;

    @Before
    public void setup() {
        mockedMission = mock(Mission.class);
    }

    @Test
    public void canStartAnInspector() throws Exception {
        final String classPath = "/home/jepeders/workspace/cern/molr/inspector/build/main/";
        final String inspectionClass = "cern.jarrace.inspector.remote.CliMain";
        final String mainClass = "cern.jarrace.inspector.remote.CliMain";
        final List<String> entryPoints = Collections.singletonList("main");
        when(mockedMission.getMoleClassName()).thenReturn(mainClass);
        when(mockedMission.getMissionContentClassName()).thenReturn(inspectionClass);
        when(mockedMission.getTasksNames()).thenReturn(entryPoints);
        final InstantiationRequest request = new InstantiationRequestImpl(classPath, mockedMission);
        SystemMain.main(new String[]{GSON.toJson(request)});
    }

}
