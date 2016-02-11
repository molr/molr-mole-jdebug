package cern.jarrace.gradle;

import cern.jarrace.agent.annotations.RunWithAgent;
import cern.jarrace.agent.impl.RunnableAgent;

/**
 * Created by jepeders on 1/19/16.
 */
@RunWithAgent(RunnableAgent.class)
public class SimpleTest2 implements Runnable {

    public void run() {
        System.err.println("Hello from test 2");
    }

}
