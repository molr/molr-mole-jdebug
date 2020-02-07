package io.molr.mole.jdebug.spawner;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.spawner.controller.JdiControllerImpl;
import io.molr.mole.jdebug.spawner.controller.LocalDelegatingStatefulJdiController;
import io.molr.mole.jdebug.spawner.controller.StatefulJdiController;
import io.molr.mole.jdebug.spawner.domain.InstantiationRequest;
import io.molr.mole.jdebug.spawner.domain.impl.InstantiationRequestImpl;
import io.molr.mole.jdebug.spawner.remote.SystemMain;

public final class JdiMissionSpawner {

    private JdiMissionSpawner() {
        /* only static methods */
    }

    public static StatefulJdiController start(JdiMission jdiMission) {
        InstantiationRequest request = new InstantiationRequestImpl(DebugMoleSpawner.CURRENT_CLASSPATH_VALUE, jdiMission);
        LocalDelegatingStatefulJdiController controller = new LocalDelegatingStatefulJdiController(jdiMission.getMissionContentClassName());
        JdiControllerImpl internal = SystemMain.startJdi(request, controller);
        controller.setDelegate(internal);
        return controller;
    }
}
