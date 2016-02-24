/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.mole.impl;

import cern.molr.commons.exception.MissionExecutionException;
import cern.molr.commons.mole.Mole;
import cern.molr.mole.annotations.MoleSpringConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link Mole} which allows for the discovery and execution of classes implementing the
 * {@link Runnable} interface using a Spring {@link ApplicationContext} to inject all the dependencies.
 * <h3>Discovery:</h3> All classes annotated with {@link Runnable} are exposed as services.
 * <h3>Execution:</h3> Allows for the execution of the {@link Runnable#run()} method and generates a Spring
 * {@link ApplicationContext} from all the resources specified by the {@link MoleSpringConfiguration} annotation.
 *
 * @author tiagomr
 * @see Mole
 */
public class RunnableSpringMole implements Mole {

    @Override
    public List<Method> discover(Class<?> classType) {
        if (null == classType) {
            throw new IllegalArgumentException("Class type cannot be null");
        }
        if (Runnable.class.isAssignableFrom(classType) && classType.getAnnotation(MoleSpringConfiguration.class) != null) {
            try {
                return Collections.singletonList(classType.getMethod("run"));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void run(String missionContentClassName, Object... args) throws MissionExecutionException {
        if (null == missionContentClassName) {
            throw new MissionExecutionException(new IllegalArgumentException("Mission content class name cannot be null"));
        }
        try {
            Class<?> missionContentClass = Class.forName(missionContentClassName);
            MoleSpringConfiguration moleSpringConfigurationAnnotation = missionContentClass.getAnnotation(MoleSpringConfiguration.class);
            if (null == moleSpringConfigurationAnnotation) {
                throw new IllegalArgumentException(String.format("Mission content class must be annotated with %s", MoleSpringConfiguration.class.getName()));
            }
            ApplicationContext context = new ClassPathXmlApplicationContext(moleSpringConfigurationAnnotation.locations());
            Object missionContentInstance = context.getBean(missionContentClass);
            if (!(missionContentInstance instanceof Runnable)) {
                throw new IllegalArgumentException(String.format("Mission content class must implement the %s interface", Runnable.class.getName()));
            }
            ((Runnable) missionContentInstance).run();
        } catch (Exception exception) {
            throw new MissionExecutionException(exception);
        }
    }
}
