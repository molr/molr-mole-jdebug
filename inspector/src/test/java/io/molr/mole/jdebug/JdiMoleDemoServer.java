package io.molr.mole.jdebug;

import cern.molr.commons.domain.JdiMission;
import io.molr.mole.core.api.Mole;
import io.molr.mole.jdebug.domain.JdiMissions;
import io.molr.mole.jdebug.mole.JdiMole;
import io.molr.mole.server.conf.SingleMoleRestServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Set;

@SpringBootApplication
@Import(SingleMoleRestServiceConfiguration.class)
public class JdiMoleDemoServer {

    private static ApplicationContext ctx;

    @Bean
    public Mole mole(Set<JdiMission> jdiMissions) {
        return new JdiMole(jdiMissions);
    }


    @Bean
    public JdiMission landTheFalcon() {
        return JdiMissions.ofMain(LandTheFalconMain.class);
    }


    public static void main(String... args) {
        if (System.getProperty("server.port") == null) {
            System.setProperty("server.port", "8800");
        }
        SpringApplication.run(JdiMoleDemoServer.class);
    }
}
