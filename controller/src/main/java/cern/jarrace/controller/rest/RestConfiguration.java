package cern.jarrace.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Created by timartin on 27/1/2016.
 */

@Configuration
@PropertySource("classpath:/server.properties")
public class RestConfiguration {

    @Autowired
    Environment environment;

    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
        JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory = new JettyEmbeddedServletContainerFactory();
        jettyEmbeddedServletContainerFactory.setPort(environment.getProperty("server.port", Integer.class));
        return jettyEmbeddedServletContainerFactory;
    }
}
