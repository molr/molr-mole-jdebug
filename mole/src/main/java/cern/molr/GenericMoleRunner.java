/*
 * � Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file �COPYING�. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr;

import cern.molr.commons.mole.Mole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the JVM's to execute {@link cern.molr.commons.domain.Mission}s
 *
 * @author jepeders
 * @author tiagomr
 */
public class GenericMoleRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericMoleRunner.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("The GenericMoleRunner#main must receive at least 2 arguments, being them" +
                    " the fully qualified domain name of the Mole to be used and the fully qualified domain name of the " +
                    "Mission to be executed");
        }

        final String moleName = args[0];
        final String missionName = args[1];

        try {
            Mole mole = createMoleInstance(moleName);
            mole.run(missionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Mole createMoleInstance(String moleName) throws Exception {
        Class<Mole> clazz = (Class<Mole>) Class.forName(moleName);
        return clazz.getConstructor().newInstance();
    }
}
