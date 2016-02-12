package cern.molr.gradle;

import cern.molr.agent.annotations.RunWithAgent;
import cern.molr.agent.annotations.AgentSpringConfiguration;
import cern.molr.agent.impl.RunnableSpringAgent;

/**
 * @author timartin
 */

@RunWithAgent(RunnableSpringAgent.class)
@AgentSpringConfiguration(locations = {"sample-bean-defenition.xml"})
public class SimpleSpringAgentImpl implements Runnable{

    @Override
    public void run() {
        System.out.println("Hello, I was injected using Spring");
    }
}
