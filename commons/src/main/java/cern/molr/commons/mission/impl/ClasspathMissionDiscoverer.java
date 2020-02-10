/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.commons.mission.impl;

import cern.molr.commons.domain.JdiMission;
import cern.molr.commons.mission.MissionMaterializer;
import cern.molr.commons.mission.MissionsDiscoverer;
import cern.molr.commons.mole.RunWithMole;
import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.Discoverer;
import com.impetus.annovention.listener.ClassAnnotationObjectDiscoveryListener;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MissionsDiscoverer} which makes use of the {@link RunWithMole} annotation to discover
 * {@link JdiMission}s using classpath scans
 *
 * @author jepeders
 * @author tiagomr
 * @author mgalilee
 */
public class ClasspathMissionDiscoverer implements MissionsDiscoverer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathMissionDiscoverer.class);
    private static final String[] SUPPORTED_ANNOTATIONS = new String[]{RunWithMole.class.getTypeName()};
    private final MissionMaterializer materializer;

    public ClasspathMissionDiscoverer(MissionMaterializer materializer) {
        this.materializer = materializer;
    }

    @Override
    public Set<JdiMission> availableMissions() {
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
                .map(materializer::materialize)
                .collect(Collectors.toSet());
    }
}
