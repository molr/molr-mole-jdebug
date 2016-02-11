package cern.jarrace.agent.impl;

import cern.jarrace.agent.Agent;
import cern.jarrace.agent.annotations.AgentSpringConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author timartin
 */
public class RunnableSpringAgent implements Agent {
    @Override
    public void initialize() {
        // Nothing to do here
    }

    @Override
    public List<Method> discover(Class<?> clazz) {
        if (Runnable.class.isAssignableFrom(clazz) && clazz.getAnnotation(AgentSpringConfiguration.class) != null) {
            try {
                return Collections.singletonList(clazz.getMethod("run"));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void run(Object... args) throws IOException {
        String entry = (String) args[0];
        try {
            Class<?> c = Class.forName(entry);
            AgentSpringConfiguration agentSpringConfigurationAnnotation = c.getAnnotation(AgentSpringConfiguration.class);
            ApplicationContext context = new ClassPathXmlApplicationContext(agentSpringConfigurationAnnotation.locations());
            Runnable runnable = (Runnable) context.getBean(c);
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
