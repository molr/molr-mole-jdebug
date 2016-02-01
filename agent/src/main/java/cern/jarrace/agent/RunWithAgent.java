/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.agent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides information about which classes shall be exposed as services and which {@link Agent} implementation must be
 * used to execute the discovery and execution methods.
 *
 * @author tiagomr
 * @author jepeders
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunWithAgent {
    /**
     * @return {@link Agent} implementation to be used for the discovery and execution
     */
    public Class<? extends Agent> value();
}
