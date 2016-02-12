package cern.molr.controller.server;

import cern.molr.controller.server.impl.ControllerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by timartin on 4/2/2016.
 */

@Configuration
@PropertySource("classpath:/server.properties")
public class ServerConfiguration {

    @Bean
    public Controller server() {
        return new ControllerImpl();
    }
}
