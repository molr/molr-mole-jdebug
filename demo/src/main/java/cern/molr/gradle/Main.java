package cern.molr.gradle;

import cern.molr.agent.impl.RunnableSpringMole;

import java.io.IOException;

/**
 * @author timartin
 */
public class Main {
    public static void main(String[] args) throws IOException {
        RunnableSpringMole runnableSpringAgent = new RunnableSpringMole();
        runnableSpringAgent.run("SimpleSpringAgentImpl");
    }
}
