package cern.molr.gradle;

import cern.molr.agent.annotations.RunWithAgent;
import cern.molr.agent.impl.RunnableAgent;

/**
 * Created by jepeders on 1/19/16.
 */
@RunWithAgent(RunnableAgent.class)
public class SimpleTest2 implements Runnable {

    public void run() {
        System.err.println("Hello from test 2");
    }

}
