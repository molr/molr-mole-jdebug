/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector;

import cern.jarrace.inspector.controller.BlockingJdiController;
import cern.jarrace.inspector.entry.EntryMethod;
import org.junit.Test;

import java.io.File;

public class BlockingJdiControllerIntegrationTest {

    @Test
    public void canRunClass() throws Exception {
        final EntryMethod method = EntryMethod.ofClassAndMethod(TestInspectable.class, "run");
        final String testPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        final String mainPath = testPath.substring(0, testPath.length() - 5) + "/main";
        final String classPath = testPath + File.pathSeparator + mainPath;
        BlockingJdiController controller = BlockingJdiController.builder()
                .setInspectableMethod(method)
                .setMainClass("cern.jarrace.inspector.TestInspectable")
                .setClassPath(classPath)
                .build();
        Thread.sleep(2000);
        controller.stepForward();
    }


}
