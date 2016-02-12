package cern.molr.gradle;

import cern.molr.agent.annotations.RunWithAgent;
import cern.molr.agent.impl.JunitAgent;
import org.junit.Test;

/**
 * Created by jepeders on 1/19/16.
 */
@RunWithAgent(JunitAgent.class)
public class SimpleTest {

    @Test
    public void hello() {
        System.out.println("Hello from number 1");
    }
}
