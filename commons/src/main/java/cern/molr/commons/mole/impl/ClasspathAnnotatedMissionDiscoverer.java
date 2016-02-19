/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.domain.impl.MissionImpl;
import cern.molr.commons.mole.MissionsDiscoverer;
import cern.molr.commons.mole.Mole;
import cern.molr.commons.mole.RunWithMole;
import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.Discoverer;
import com.impetus.annovention.listener.ClassAnnotationObjectDiscoveryListener;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MissionsDiscoverer} which makes use of the {@link RunWithMole} annotation to discover
 * {@link Mission}s using classpath scans
 *
 * @author jepeders
 * @author tiagomr
 * @author mgalilee
 */
public class ClasspathAnnotatedMissionDiscoverer implements MissionsDiscoverer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathAnnotatedMissionDiscoverer.class);
    private static final String[] SUPPORTED_ANNOTATIONS = new String[]{RunWithMole.class.getTypeName()};

    @Override
    public List<Mission> availableMissions() {
        final Set<Class<?>> missionClasses = new HashSet<>();
        final Discoverer discoverer = new ClasspathDiscoverer();
        discoverer.addAnnotationListener(
                new ClassAnnotationObjectDiscoveryListener() {
                    @Override
                    public void discovered(ClassFile classFile, Annotation annotation) {
                        try {
                            missionClasses.add(Class.forName(classFile.getName()));
                        } catch (ClassNotFoundException classNotFoundException) {
                            LOGGER.error("Could not get class from classFile", classFile, classNotFoundException);
                        }
                    }

                    @Override
                    public String[] supportedAnnotations() {
                        return SUPPORTED_ANNOTATIONS;
                    }
                }
        );
        discoverer.discover(true, false, false, false, true, true);
        return missionClasses.stream()
                .map(this::toMission)
                .collect(Collectors.toList());
    }

    private Mission toMission(Class<?> moleAnnotatedClass) {
        RunWithMole moleAnnotation = moleAnnotatedClass.getAnnotation(RunWithMole.class);
        Class<? extends Mole> moleClass = moleAnnotation.value();
        Mole mole = instantiateMole(moleClass);
        String moleClassName = moleClass.getName();
        List<String> methodsNames = mole.discover(moleAnnotatedClass).stream()
                .map(Method::getName)
                .collect(Collectors.toList());
        return new MissionImpl(moleClassName, moleAnnotatedClass.getName(), methodsNames);
    }

    private static Mole instantiateMole(final Class<? extends Mole> moleClass) {
        Mole mole = null;
        try {
            Constructor<? extends Mole> constructor = moleClass.getConstructor();
            mole = constructor.newInstance();
        } catch (Exception exception) {
            LOGGER.error("Could not instantiate Mole of class", moleClass, exception);
        }
        return mole;
    }
}
