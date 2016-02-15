package cern.molr.gradle;

import cern.molr.agent.annotations.RunWithMole;
import cern.molr.agent.impl.JunitMole;
import org.junit.Test;

/**
 * Created by jepeders on 1/19/16.
 */
@RunWithMole(JunitMole.class)
public class SimpleTest {

    @Test
    public void hello() {
        System.out.println("Hello from number 1");
    }
}
