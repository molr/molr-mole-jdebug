package cern.molr.agent.impl;

import cern.molr.agent.Mole;
import cern.molr.agent.annotations.MoleSpringConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author timartin
 */
public class RunnableSpringMole implements Mole {
    @Override
    public void initialize() {
        // Nothing to do here
    }

    @Override
    public List<Method> discover(Class<?> clazz) {
        if (Runnable.class.isAssignableFrom(clazz) && clazz.getAnnotation(MoleSpringConfiguration.class) != null) {
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
            MoleSpringConfiguration moleSpringConfigurationAnnotation = c.getAnnotation(MoleSpringConfiguration.class);
            ApplicationContext context = new ClassPathXmlApplicationContext(moleSpringConfigurationAnnotation.locations());
            Runnable runnable = (Runnable) context.getBean(c);
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
