/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.agent;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface that allows for the dynamic creation of agents. Agents are responsible for the discovery of entry points
 * that will be exposed to be executed.
 *
 * @author tiagomr
 */
public interface Agent {

    public void initialize();

    /**
     * Discover the entry points that will be registered
     * @return {@link List} of endpoints
     */
    List<Method> discover(Class<?> clazz);

    /**
     * Executes a service/entry point(s)
     */
    void run(Object... args) throws IOException;
}
