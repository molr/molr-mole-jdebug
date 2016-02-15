/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.agent;

import cern.molr.agent.annotations.RunWithMole;
import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.Discoverer;
import com.impetus.annovention.listener.ClassAnnotationObjectDiscoveryListener;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jepeders
 */
public class MoleDiscoverer {

    private static final String[] SUPPORTED_ANNOTATIONS = new String[]{"RunWithMole"};

    private MoleDiscoverer() {
        /* Should not be instantiated */
    }

    public static void discover(Map<Mole, Map<Class<?>, List<Method>>> agents) {
        Discoverer discoverer = new ClasspathDiscoverer();
        discoverer.addAnnotationListener(
                new ClassAnnotationObjectDiscoveryListener() {
                     @Override
                     public void discovered(ClassFile clazz, Annotation annotation) {
                         try {
                            System.out.println("Found " + clazz);
                             Class<?> mClazz = Class.forName(clazz.getName());
                             RunWithMole agentAnnotation = mClazz.getAnnotation(RunWithMole.class);
                             Mole mole = agents.keySet().stream()
                                     .filter((Mole key) -> key.getClass().equals(agentAnnotation.value())).findAny().orElseGet(() -> {
                                         Constructor constructor = null;
                                         try {
                                             constructor = agentAnnotation.value().getConstructor();
                                             Mole newMole = (Mole) constructor.newInstance();
                                             newMole.initialize();
                                             agents.put(newMole, new HashMap<>());
                                             return newMole;
                                         } catch ( Exception e) {
                                             e.printStackTrace();
                                         }

                                         return null;
                                     });
                             Map<Class<?>, List<Method>> agentMethods = agents.get(mole);
                             try {
                                 Class<?> classDefinition = Class.forName(clazz.getName());
                                 agentMethods.put(classDefinition, mole.discover(classDefinition));
                             } catch (ClassNotFoundException e) {
                                 e.printStackTrace();
                             }
                         } catch (ClassNotFoundException e) {
                             e.printStackTrace();
                         }
                     }

                     @Override
                     public String[] supportedAnnotations () {
                         return SUPPORTED_ANNOTATIONS;
                     }
                 }

        );
        System.out.println("Starting Discovery");
        discoverer.discover(true,false,false,false,true,true);
        System.out.println("Finished Discovery");
    }

}
