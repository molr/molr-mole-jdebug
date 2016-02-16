/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.impl;

import cern.molr.mole.Mole;
import cern.molr.TaskDiscoverer;
import cern.molr.mole.annotations.RunWithMole;
import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.Discoverer;
import com.impetus.annovention.listener.ClassAnnotationObjectDiscoveryListener;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author jepeders
 * @author tiagomr
 */
public class ClasspathAnnotatedTaskDiscoverer implements TaskDiscoverer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathAnnotatedTaskDiscoverer.class);
    private static final String[] SUPPORTED_ANNOTATIONS = new String[]{"RunWithMole"};

    public Map<Mole, Map<Class<?>, List<Method>>> discover() {
        final Map<Mole, Map<Class<?>, List<Method>>> moles = new HashMap<>();
        final Discoverer discoverer = new ClasspathDiscoverer();
        discoverer.addAnnotationListener(
                new ClassAnnotationObjectDiscoveryListener() {
                    @Override
                    public void discovered(ClassFile clazz, Annotation annotation) {
                        try {
                            Class<?> mClazz = Class.forName(clazz.getName());
                            RunWithMole moleAnnotation = mClazz.getAnnotation(RunWithMole.class);
                            tryGetOrCreateMole(moles, moleAnnotation.value())
                                    .ifPresent(mole -> {
                                        Map<Class<?>, List<Method>> agentMethods = moles.get(mole);
                                        try {
                                            Class<?> classDefinition = Class.forName(clazz.getName());
                                            agentMethods.put(classDefinition, mole.discover(classDefinition));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    });
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public String[] supportedAnnotations() {
                        return SUPPORTED_ANNOTATIONS;
                    }
                }
        );
        discoverer.discover(true, false, false, false, true, true);
        return moles;
    }

    private static final Optional<Mole> tryGetOrCreateMole(final Map<Mole, Map<Class<?>, List<Method>>> moles,
                                                           final Class<? extends Mole> moleClass) {
        return Optional.ofNullable(moles.keySet().stream()
                .filter(mole -> mole.getClass().equals(moleClass))
                .findFirst()
                .orElse(instantiateMole(moleClass)));
    }

    private static final Mole instantiateMole(final Class<? extends Mole> moleClass) {
        Mole mole = null;
        try {
            Constructor<? extends Mole> constructor = moleClass.getConstructor();
            mole = (Mole) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mole;
    }
}
