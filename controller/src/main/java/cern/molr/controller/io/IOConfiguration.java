/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.io;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring {@link Configuration} file for defining io related beans
 *
 * @author tiagomr
 */
@Configuration
@PropertySource("classpath:/server.properties")
public class IOConfiguration {
    @Bean
    public JarWriter jarWriter() {
        return new JarWriter();
    }
}
