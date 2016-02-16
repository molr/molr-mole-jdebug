package cern.molr.gradle;

import cern.molr.commons.mole.RunWithMole;
import cern.molr.mole.impl.RunnableMole;

/**
 * Created by jepeders on 1/19/16.
 */
@RunWithMole(RunnableMole.class)
public class SimpleTest2 implements Runnable {

    public void run() {
        System.err.println("Hello from test 2");
    }

}
