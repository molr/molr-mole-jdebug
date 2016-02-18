package cern.molr.commons.domain;

import java.util.List;

/**
 * A generic service, encapsulating the minimum information needed to spawn and run tasks within a specific
 * {@link cern.molr.commons.mole.Mole}
 *
 * @author mgalilee
 */
public interface Service {

    /**
     * @return the type of {@link cern.molr.commons.mole.Mole} which will run the tasks of this service
     */
    String getMoleClassName();

    /**
     * @return the full name of the class which provides the tasks
     */
    String getServiceClassName();

    /**
     * @return the list of tasks this service provides
     */
    List<String> getTasksNames();
}
