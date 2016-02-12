/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.commons.domain;

import java.util.ArrayList;
import java.util.List;

public class Service {

    /**
     * Name of the agent that can execute the service
     */
    private String agentName;

    /**
     * Class used by the exposed {@link Service}
     */
    private String className;

    /**
     * {@link List} of entry points exposed by this service in the specific class
     */
    private final List<String> entryPoints = new ArrayList<>();

    public Service() {
    }

    public Service(String agentName, String className) {
        this.agentName = agentName;
        this.className = className;
    }

    public Service(String agentName, String className, List<String> entryPoints) {
        this.agentName = agentName;
        this.className = className;
        this.entryPoints.addAll(entryPoints);
    }

    public String getAgentName() {
        return agentName;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getEntryPoints() {
        return new ArrayList<>(entryPoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        if (agentName != null ? !agentName.equals(service.agentName) : service.agentName != null) return false;
        return !(className != null ? !className.equals(service.className) : service.className != null);

    }

    @Override
    public int hashCode() {
        int result = agentName != null ? agentName.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getAgentName() + ": " + getClassName();
    }

}