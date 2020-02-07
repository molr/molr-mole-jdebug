/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.domain;

import java.util.List;

/**
 * Interface that encapsulates the minimum information needed to spawn and execute tasks using a {@link cern.molr.commons.mole.Mole}
 *
 * @author mgalilee
 * @author tiagomr
 */
public interface JdiMission {

    /**
     * @return A {@link String} with the fully qualified domain name to the type of {@link cern.molr.commons.mole.Mole}
     * which will run the tasks of this {@link JdiMission}.
     */
    String getMoleClassName();

    /**
     * @return A {@link String} with the fully qualified domain name of the class which provides the execution
     * behaviour.
     */
    String getMissionContentClassName();

    /**
     * @return A {@link List} of {@link String}s with the tasks extracted from the given mission content class.
     */
    List<String> getTasksNames();
}
