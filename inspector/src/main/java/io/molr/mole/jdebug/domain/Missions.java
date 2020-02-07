package io.molr.mole.jdebug.domain;

import cern.molr.commons.domain.JdiMission;
import cern.molr.commons.domain.impl.MissionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Utility class that provides factory methods for mission domain objects.
 * These domain objects describe the class to execute and the entry points.
 * </p>
 * These objects are then later fed into a {@link io.molr.mole.jdebug.spawner.controller.JdiControllerImpl} for instantiation.
 */
public final class Missions {

    private final static Logger LOGGER = LoggerFactory.getLogger(Missions.class);

    private Missions() {
        /* Only static methods */
    }

    public static JdiMission ofMain(Class<?> mainClass) {
        requireNonNull(mainClass, "mainClass must not be null.");
        Optional<Method> mainMethod = mainMethod((Class<?>) mainClass);
        if (!mainMethod.isPresent()) {
            throw new IllegalArgumentException("Class '" + mainClass + "' does not contain a valid main method! Cannot create a mission from it.");
        }
        return new MissionImpl(mainClass.getName(), mainClass.getName(), Collections.singletonList(mainMethod.get().getName()));
    }

    private static Optional<Method> mainMethod(Class<?> mainClass) {
        return searchByName(mainClass) //
                .filter(m -> Modifier.isStatic(m.getModifiers())) //
                .filter(m -> m.getReturnType().equals(Void.TYPE));
    }

    private static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    private static Optional<Method> searchByName(Class<?> mainClass) {
        try {
            return Optional.of(mainClass.getDeclaredMethod("main", String[].class));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }


}
