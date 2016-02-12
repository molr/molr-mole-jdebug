package cern.molr.gradle;

import cern.molr.agent.impl.RunnableSpringAgent;

import java.io.IOException;

/**
 * @author timartin
 */
public class Main {
    public static void main(String[] args) throws IOException {
        RunnableSpringAgent runnableSpringAgent = new RunnableSpringAgent();
        runnableSpringAgent.run("SimpleSpringAgentImpl");
    }
}
