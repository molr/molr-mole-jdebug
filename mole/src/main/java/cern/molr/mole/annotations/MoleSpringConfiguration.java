/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.mole.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used by the {@link cern.molr.mole.impl.RunnableSpringMole} to generate the
 * {@link org.springframework.context.ApplicationContext}
 *
 * @author tiagomr
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MoleSpringConfiguration {

    /**
     * @return An array of {@link String}s with all the resources to be used by the Spring injection engine
     */
    public String[] locations() default {};
}
