/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.rest.controller;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.io.JarWriter;
import cern.jarrace.controller.jvm.AgentContainerSpawner;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import cern.jarrace.controller.manager.AgentContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Optional;
import java.util.Set;

/**
 * {@link RestController} that exposes REST endpoints to manage {@link cern.jarrace.controller.domain.AgentContainer}s
 * @author tiagomr
 */
@RestController
@RequestMapping("${rest.basepath}/container")
public class AgentContainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentContainerController.class);
    private static final File DEPLOYMENT_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String CONTAINER_NAME_VARIABLE_NAME = "containerName";

    @Autowired
    private AgentContainerManager agentContainerManager;
    @Autowired
    private AgentContainerSpawner agentContainerSpawner;
    @Autowired
    private AgentRunnerSpawner agentRunnerSpawner;
    @Autowired
    private JarWriter jarWriter;


    @RequestMapping(value = "/deploy/{" + CONTAINER_NAME_VARIABLE_NAME + "}", method = RequestMethod.POST)
    public void deploy(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName, @RequestBody byte[] jar) throws Exception {
        LOGGER.debug("Started deployment process for container: [{}]", containerName);
        String path = jarWriter.writeFile(containerName, jar);
        agentContainerSpawner.spawnAgentContainer(containerName, path);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerService(@RequestBody AgentContainer agentContainer){
        agentContainerManager.registerAgentContainer(agentContainer);
        LOGGER.info("Registered new AgentContainer: [{}]", agentContainer);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<AgentContainer> listContainers() {
        return agentContainerManager.getAgentContainers();
    }

    @RequestMapping(value = "/{containerName}/start", method = RequestMethod.GET)
    public void runService(@PathVariable("containerName") String containerName,
                           @RequestParam(value = "service") String serviceName,
                           @RequestParam(value = "entryPoints", defaultValue = "") String entryPoints) throws Exception {
        AgentContainer agentContainer = agentContainerManager.getAgentContainer(containerName);
        Optional<Service> serviceOptional = agentContainer.getServices().stream().filter(service -> {
            return service.getClazz().equals(serviceName) ? true : false;
        }).findFirst();
        if(serviceOptional.isPresent()) {

            agentRunnerSpawner.spawnAgentContainer(serviceOptional.get(), agentContainer.getContainerPath(),
                    entryPoints.split(","));
        }
    }
}
