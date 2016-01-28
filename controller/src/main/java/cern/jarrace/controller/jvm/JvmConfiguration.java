package cern.jarrace.controller.jvm;

import cern.jarrace.controller.jvm.impl.SimpleAgentRegistrySpawner;
import cern.jarrace.controller.jvm.impl.SimpleAgentRunnerSpawner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by timartin on 28/01/2016.
 */
@Configuration
public class JvmConfiguration {
    @Bean
    public AgentRegistrySpawner agentContainerSpawner() {
        return new SimpleAgentRegistrySpawner();
    }

    @Bean
    public AgentRunnerSpawner agentRunnerSpawner() {
        return new SimpleAgentRunnerSpawner();
    }
}
