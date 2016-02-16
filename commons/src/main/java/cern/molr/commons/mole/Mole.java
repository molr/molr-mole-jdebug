/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.commons.mole;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface that allows for the dynamic creation of {@link Mole}s. {@link Mole}s are responsible for the discovery and
 * execution behaviour of tasks.
 *
 * @author tiagomr
 */
public interface Mole {

    /**
     * Scans a specific {@link Class} for tasks to be executed by this {@link Mole} specific implementation
     *
     * @param clazz {@link Class} to be scanned for tasks
     * @return {@link List} with all the methods that can be executed by the specific implementation
     */
    List<Method> discover(Class<?> clazz);

    /**
     * Executes a
     */
    void run(Object... args) throws IOException;
}
