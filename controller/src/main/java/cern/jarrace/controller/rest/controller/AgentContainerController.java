/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.rest.controller;

import cern.jarrace.controller.domain.AgentContainer;
import cern.jarrace.controller.jvm.AgentContainerSpawner;
import cern.jarrace.controller.manager.AgentContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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


    @RequestMapping(value = "/deploy/{" + CONTAINER_NAME_VARIABLE_NAME + "}", method = RequestMethod.POST)
    public void deploy(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName, @RequestBody byte[] jar) throws Exception {
        LOGGER.debug("Started deployment process for container: [{}]", containerName);
        String path = writeFile(containerName, jar);
        agentContainerSpawner.spawnJvm(containerName, path);
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
/*
    @RequestMapping(value = "/{containerName}/service/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Service> listServices(@PathVariable(value = "containerName") String containerName) {
        List<Service> toReturn = this.entryPoints.get(containerName);
        if (toReturn == null) {
            toReturn = new ArrayList<>();
        }
        return toReturn;
    }

    @RequestMapping(value = "/{name}/{entrypoint}/start", method = RequestMethod.GET)
    public String runService(@PathVariable("name") String name, @PathVariable int entrypoint) throws IOException {
        List<Service> entries = entryPoints.get(name);
        Service entryPoint = entries.get(entrypoint);
        List<String> args = new ArrayList<>();
        args.add("cern.jarrace.agent.AgentRunner");
        args.add(entryPoint.getAgentName());
        args.add(entryPoint.getClazz());
        args.add(entryPoint.getEntryPoints().stream().collect(Collectors.joining(",")));
        startContainer(paths.get(name), args);
        return null;
    }
*/

    private String writeFile(String name, byte[] jar) throws IOException {
        File deploymentFile = DEPLOYMENT_DIR.toPath().resolve(name + ".jar").toFile();
        if (deploymentFile.exists()) {
            deploymentFile.delete();
        }
        deploymentFile.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(deploymentFile);
        outputStream.write(jar);
        outputStream.close();
        LOGGER.info("Deployed file + " + deploymentFile.getAbsolutePath());
        return deploymentFile.getAbsolutePath();
    }
}
