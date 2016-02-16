/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.jvm;

import cern.molr.jvm.impl.SimpleMoleRegistrySpawner;
import cern.molr.jvm.impl.SimpleMoleRunnerSpawner;
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
    public MoleRegistrySpawner agentContainerSpawner() {
        return new SimpleMoleRegistrySpawner();
    }

    @Bean
    public MoleRunnerSpawner agentRunnerSpawner() {
        return new SimpleMoleRunnerSpawner();
    }
}
