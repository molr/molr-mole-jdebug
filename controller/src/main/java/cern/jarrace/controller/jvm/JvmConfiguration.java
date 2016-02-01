/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.jvm;

import cern.jarrace.controller.jvm.impl.SimpleAgentRegistrySpawner;
import cern.jarrace.controller.jvm.impl.SimpleAgentRunnerSpawner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring {@link Configuration} file for defining JVM spawn related beans
 *
 * @author tiagomr
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
