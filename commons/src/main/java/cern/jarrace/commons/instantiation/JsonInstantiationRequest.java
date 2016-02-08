/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.commons.instantiation;

import cern.jarrace.commons.domain.Service;
import com.google.gson.Gson;

/**
 * A JSON implementation of the {@link InspectorInstantiationRequest}.
 */
public class JsonInstantiationRequest implements InspectorInstantiationRequest {

    private final String classPath;
    private final Service entryPoints;

    public JsonInstantiationRequest(String classPath, Service entryPoints) {
        this.classPath = classPath;
        this.entryPoints = entryPoints;
    }

    @Override
    public String getClassPath() {
        return classPath;
    }

    @Override
    public Service getEntryPoints() {
        return entryPoints;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static JsonInstantiationRequest fromJson(String input) {
        return new Gson().fromJson(input, JsonInstantiationRequest.class);
    }

    public static class Builder {

        private String classPath;
        private String mainClass;
        private String entryPoint;

        public Builder setClassPath(String classPath) {
            this.classPath = classPath;
            return this;
        }

        public Builder setEntryPoint(String entryPoint) {
            this.entryPoint = entryPoint;
            return this;
        }

        public Builder setMainClass(String mainClass) {
            this.mainClass = mainClass;
            return this;
        }

    }

}
