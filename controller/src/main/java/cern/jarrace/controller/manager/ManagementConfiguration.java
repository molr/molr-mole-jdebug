/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.manager;

import cern.jarrace.controller.manager.impl.InMemoryAgentContainerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring {@link Configuration} file for defining management related beans
 *
 * @author tiagomr
 */

@Configuration
public class ManagementConfiguration {
    @Bean
    public AgentContainerManager agentContainerManager() {
        return new InMemoryAgentContainerManager();
    }
}
