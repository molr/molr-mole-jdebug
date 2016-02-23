/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.mole.impl;

import cern.molr.commons.mole.Mole;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link Mole} which allows for the discovery and execution of classes implementing the
 * {@link Runnable} interface.
 * <h3>Discovery:</h3> All classes annotated with {@link Runnable} are exposed as services.
 * <h3>Execution:</h3> Allows for the execution of the {@link Runnable#run()} entry point.
 *
 * @author tiagomr
 * @see Mole
 */
public class RunnableMole implements Mole {

    @Override
    public List<Method> discover(Class<?> clazz) {
        if (Runnable.class.isAssignableFrom(clazz)) {
            try {
                return Collections.singletonList(clazz.getMethod("run"));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void run(String missionName, Object... args) throws IOException {
        try {
            Class<?> c = Class.forName(missionName);
            Runnable runnable = (Runnable) c.getConstructor().newInstance();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
