/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr;

import cern.molr.mole.annotations.MoleSpringConfiguration;
import org.junit.Test;

/**
 * Test definitions common to several test suites
 *
 * @author tiagomr
 */
public class TestDefinitions {

    public static class JunitMission {
        @Test
        public void mission1() {
        }

        @Test
        public void mission2() {
        }

        public void mission3() {
        }
    }

    public static class RunnableMission implements Runnable {
        @Override
        public void run() {
        }

        public void otherMethod() {

        }
    }

    @MoleSpringConfiguration(locations = {"test-bean-definition.xml"})
    public static class RunnableSpringMission implements Runnable {
        @Override
        public void run() {
        }

        public void otherMethod() {
        }
    }

    public static class EmptyMission {
    }
}
