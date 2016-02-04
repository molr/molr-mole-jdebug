/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui.rest;

import cern.jarrace.commons.domain.AgentContainer;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

/**
 * A service that can access a remote endpoint with methods related to {@link AgentContainer}s.
 */
public interface ContainerService {

    /**
     * @return A list of {@link AgentContainer}s available in the remote server.
     */
    @GET("container/list")
    Call<List<AgentContainer>> getContainers();

    /**
     * Starts an entry within the given container and returns the result of the execution as a String.
     *
     * @param name    The name of the container to run.
     * @param service The entry point inside the container.
     * @return The output of the process.
     */
    @GET("container/{containerName}/start/")
    Call<String> startEntry(@Path("containerName") String name, @Query("service") String service);

    /**
     * Reads a class from inside the container with the given name.
     *
     * @param containerName The name of the container to read.
     * @param className     The name of the class inside the container to read. The name should be the full reverse-domain
     *                      name of the class, excluding any suffixes. Example: <code>cern.test.ClassName</code>
     * @return The content of the class as a String.
     */
    @GET("container/{containerName}/read")
    Call<String> readClass(@Path("containerName") String containerName, @Query("class") String className);
}
