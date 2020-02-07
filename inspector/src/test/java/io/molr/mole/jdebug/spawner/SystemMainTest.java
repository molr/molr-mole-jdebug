/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package io.molr.mole.jdebug.spawner;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.domain.InstantiationRequest;
import io.molr.mole.jdebug.spawner.domain.impl.InstantiationRequestImpl;
import io.molr.mole.jdebug.spawner.json.MissionTypeAdapter;
import io.molr.mole.jdebug.spawner.remote.SystemMain;
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
            .registerTypeAdapter(JdiMission.class, new MissionTypeAdapter().nullSafe())
            .create();

    private JdiMission mockedJdiMission;

    @Before
    public void setup() {
        mockedJdiMission = mock(JdiMission.class);
    }

    @Test
    public void canStartAnInspector() throws Exception {
        final String classPath = "/home/jepeders/workspace/cern/molr/inspector/build/main/";
        final String inspectionClass = "cern.jarrace.inspector.remote.CliMain";
        final String mainClass = "cern.jarrace.inspector.remote.CliMain";
        final List<String> entryPoints = Collections.singletonList("main");
        when(mockedJdiMission.getMoleClassName()).thenReturn(mainClass);
        when(mockedJdiMission.getMissionContentClassName()).thenReturn(inspectionClass);
        when(mockedJdiMission.getTasksNames()).thenReturn(entryPoints);
        final InstantiationRequest request = new InstantiationRequestImpl(classPath, mockedJdiMission);
        SystemMain.main(new String[]{GSON.toJson(request)});
    }

}
