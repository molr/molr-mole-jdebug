package cern.jarrace.gradle;

import cern.jarrace.agent.RunWithAgent;
import cern.jarrace.agent.impl.JunitAgent;
import cern.jarrace.agent.impl.RunnableAgent;
import org.junit.Test;

/**
 * Created by jepeders on 1/19/16.
 */
@RunWithAgent(RunnableAgent.class)
public class SimpleTest2 implements Runnable {

    public void run() {
        System.err.println("Hello from test 2");
    }

}
