/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.molr.agent.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import cern.molr.agent.Agent;

public class SpringAgent implements Agent{

    @Override
    public void initialize() {
        // Nothing to do here
    }

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
    public void run(Object... args) throws IOException {
        String entry = (String) args[0];
        try {
            Class<?> c = Class.forName(entry);
            Runnable runnable = (Runnable) c.getConstructor().newInstance();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}
