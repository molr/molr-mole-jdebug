package cern.jarrace.controller.server;

import cern.jarrace.controller.server.impl.ServerImpl;
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
    public Server server() {
        return new ServerImpl();
    }
}
