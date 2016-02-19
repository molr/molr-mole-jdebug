/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.commons.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for the representation of a deployment to the controller. It couples all necessary information to
 * identify a single deployment and all the {@link Mission}s provided by it and later execute them.
 *
 * @author tiagomr
 */
public class MoleContainer {

    /**
     * Unique identifier of each MoleContainer, this field must always be unique
     */
    private String containerName;

    /**
     * Identifies the binary file sent in the deployment process
     */
    private String containerPath;

    /**
     * A {@link List} of {@link Mission}s that is provided by this {@link MoleContainer}, and can be executed
     */
    private final List<Mission> missions = new ArrayList();

    public MoleContainer() {

    }

    public MoleContainer(String containerName, String containerPath, List<Mission> missions) {
        this.containerName = containerName;
        this.containerPath = containerPath;
        this.missions.addAll(missions);
    }

    public String getContainerName() {
        return containerName;
    }

    public String getContainerPath() {
        return containerPath;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoleContainer that = (MoleContainer) o;
        return !(containerName != null ? !containerName.equals(that.containerName) : that.containerName != null);

    }

    @Override
    public int hashCode() {
        return containerName != null ? containerName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getContainerName();
    }

}
