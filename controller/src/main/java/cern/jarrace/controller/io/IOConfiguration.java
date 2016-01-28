package cern.jarrace.controller.io;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by timartin on 28/01/2016.
 */
@Configuration
public class IOConfiguration {
    @Bean
    public JarWriter jarWriter() {
        return new JarWriter();
    }
}
