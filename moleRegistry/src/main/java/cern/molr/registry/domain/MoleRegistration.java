package cern.molr.registry.domain;

import cern.molr.commons.domain.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author timartin
 */
public class MoleRegistration {

    private final String host;
    private final String moleClassName;
    private final List<Service> services;

    public MoleRegistration(String host, String moleClassName, List<Service> services) {
        this.host = host;
        this.moleClassName = moleClassName;
        this.services = new ArrayList<>(services);
    }

    public String getHost() {
        return host;
    }

    public String getMoleClassName() {
        return moleClassName;
    }

    public List<Service> getServices() {
        return services;
    }
}
