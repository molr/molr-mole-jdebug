package cern.jarrace.gradle;

import cern.jarrace.agent.impl.RunnableSpringAgent;

import java.io.IOException;

/**
 * @author timartin
 */
public class Main {
    public static void main(String[] args) throws IOException {
        RunnableSpringAgent runnableSpringAgent = new RunnableSpringAgent();
        runnableSpringAgent.run("cern.jarrace.gradle.SimpleSpringAgentImpl");
    }
}
