/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.commons.domain.impl;

import cern.molr.commons.domain.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceImpl implements Service {

    /**
     * Name of the mole that can execute the service
     */
    private String moleClassName;

    /**
     * Class used by the exposed {@link Service}
     */
    private String serviceClassName;

    /**
     * {@link List} of entry points exposed by this service in the specific class
     */
    private final List<String> entryPoints = new ArrayList<>();

    public ServiceImpl() {
    }

    public ServiceImpl(String moleClassName, String serviceClassName) {
        this.moleClassName = moleClassName;
        this.serviceClassName = serviceClassName;
    }

    public ServiceImpl(String moleClassName, String serviceClassName, List<String> entryPoints) {
        this.moleClassName = moleClassName;
        this.serviceClassName = serviceClassName;
        this.entryPoints.addAll(entryPoints);
    }

    @Override
    public String getMoleClassName() {
        return moleClassName;
    }

    @Override
    public String getServiceClassName() {
        return serviceClassName;
    }

    @Override
    public List<String> getTasksNames() {
        return new ArrayList<>(entryPoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceImpl service = (ServiceImpl) o;
        if (moleClassName != null ? !moleClassName.equals(service.moleClassName) : service.moleClassName != null) return false;
        return !(serviceClassName != null ? !serviceClassName.equals(service.serviceClassName) : service.serviceClassName != null);

    }

    @Override
    public int hashCode() {
        int result = moleClassName != null ? moleClassName.hashCode() : 0;
        result = 31 * result + (serviceClassName != null ? serviceClassName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getMoleClassName() + ": " + getServiceClassName();
    }

}