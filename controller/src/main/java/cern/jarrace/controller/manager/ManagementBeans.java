package cern.jarrace.controller.manager;

import cern.jarrace.controller.manager.impl.InMemoryAgentContainerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by tiago on 1/27/16.
 */

@Configuration
public class ManagementBeans {
    @Bean
    public AgentContainerManager agentContainerManager() {
        return new InMemoryAgentContainerManager();
    }
}
