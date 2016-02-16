package cern.molr.gradle;

import cern.molr.mole.annotations.RunWithMole;
import cern.molr.mole.annotations.MoleSpringConfiguration;
import cern.molr.mole.impl.RunnableSpringMole;

/**
 * @author timartin
 */

@RunWithMole(RunnableSpringMole.class)
@MoleSpringConfiguration(locations = {"sample-bean-defenition.xml"})
public class SimpleSpringAgentImpl implements Runnable{

    @Override
    public void run() {
        System.out.println("Hello, I was injected using Spring");
    }
}
