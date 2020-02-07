package io.molr.mole.jdebug.spawner;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.jdebug.domain.Missions;
import io.molr.mole.jdebug.spawner.controller.StatefulJdiController;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class DebugMoleSpawnerTest {

    @Test
    public void firstSpawning() throws InterruptedException {
        JdiMission jdiMission = Missions.ofMain(PrimitiveTestingMain.class);
        Assertions.assertThat(jdiMission).isNotNull();

        StatefulJdiController controller = JdiMissionSpawner.start(jdiMission);
        Assertions.assertThat(controller).isNotNull();


        CountDownLatch finished = new CountDownLatch(1);
        while (true) {
            Thread.sleep(1000);
            if (controller.isDead()) {
                break;
            }
            controller.stepForward();
        }

        //finished.await();

    }

}
