package cern.jarrace.controller.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author timartin
 * Domain class that represents a registered {@link Service}
 */

public class Service {

    /**
     * Name of the agent that can execute the service
     */
    String agentName;

    /**
     * Class used by the exposed {@link Service}
     */
    String clazz;

    /**
     * {@link List} of endpoints exposed by this service in the specific class
     */
    List<String> endpoints;

    public Service() {
        endpoints = new ArrayList<>();
    }

    public Service(String agentName, String clazz) {
        this.agentName = agentName;
        this.clazz = clazz;
        endpoints = new ArrayList<>();
    }

    public Service(String agentName, String clazz, List<String> endpoints) {
        this.agentName = agentName;
        this.clazz = clazz;
        this.endpoints = endpoints;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }
}