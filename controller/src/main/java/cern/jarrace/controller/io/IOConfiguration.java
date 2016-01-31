package cern.jarrace.controller.io;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by timartin on 28/01/2016.
 */
@Configuration
@PropertySource("classpath:/server.properties")
public class IOConfiguration {
    @Bean
    public JarWriter jarWriter() {
        return new JarWriter();
    }
}
