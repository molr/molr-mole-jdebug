package cern.molr.gradle;

import cern.molr.mole.annotations.RunWithMole;
import cern.molr.mole.impl.JunitMole;
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
