package cern.jarrace.gradle;

import cern.jarrace.agent.annotations.RunWithAgent;
import cern.jarrace.agent.annotations.AgentSpringConfiguration;
import cern.jarrace.agent.impl.RunnableSpringAgent;

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
