/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.rest.controller;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.io.JarReader;
import cern.jarrace.controller.io.JarWriter;
import cern.jarrace.controller.jvm.AgentRegistrySpawner;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import cern.jarrace.controller.manager.AgentContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * {@link RestController} that exposes endpoints to manage {@link AgentContainer}s
 *
 * @author tiagomr
 */
@RestController
@RequestMapping("${rest.basepath}/container")
public class AgentContainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentContainerController.class);
    private static final File DEPLOYMENT_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String CONTAINER_NAME_VARIABLE_NAME = "containerName";
    static final String JAVA_CLASS_SUFFIX = ".java";

    @Autowired
    private AgentContainerManager agentContainerManager;
    @Autowired
    private AgentRegistrySpawner agentRegistrySpawner;
    @Autowired
    private AgentRunnerSpawner agentRunnerSpawner;
    @Autowired
    private JarWriter jarWriter;


    @RequestMapping(value = "/deploy/{" + CONTAINER_NAME_VARIABLE_NAME + "}", method = RequestMethod.POST)
    public void deploy(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName, @RequestBody byte[] jar) throws Exception {
        LOGGER.debug("Started deployment process for container: [{}]", containerName);
        String path = jarWriter.writeFile(containerName, jar);
        agentRegistrySpawner.spawnAgentRegistry(containerName, path);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerService(@RequestBody AgentContainer agentContainer) {
        agentContainerManager.registerAgentContainer(agentContainer);
        LOGGER.info("Registered new AgentContainer: [{}]", agentContainer);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<AgentContainer> listContainers() {
        Set<AgentContainer> allAgentContainers = agentContainerManager.findAllAgentContainers();
        return allAgentContainers;
    }

    @RequestMapping(value = "/{" + CONTAINER_NAME_VARIABLE_NAME + "}/start", method = RequestMethod.GET)
    public String runService(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName,
                             @RequestParam(value = "service") String serviceName,
                             @RequestParam(value = "entryPoints", defaultValue = "") String entryPoints) throws Exception {
        Optional<AgentContainer> optionalAgentContainer = agentContainerManager.findAgentContainer(containerName);
        if (!optionalAgentContainer.isPresent()) {
            throw new IllegalArgumentException("AgentContainer name must exist");
        }
        AgentContainer agentContainer = optionalAgentContainer.get();
        Optional<Service> serviceOptional = agentContainer.getServices().stream().filter(service -> {
            String className = service.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            return className.equals(serviceName);
        }).findFirst();
        if (serviceOptional.isPresent()) {
            Service service = serviceOptional.get();
            List<String> parsedEntryPoints = Arrays.asList(entryPoints.split(","));
            parsedEntryPoints.forEach(entryPoint -> {
                if (!service.getEntryPoints().contains(entryPoint)) {
                    throw new IllegalArgumentException("All entry points must exist");
                }
            });
            return agentRunnerSpawner.spawnAgentRunner(service, agentContainer.getContainerPath(),
                    parsedEntryPoints);
        }
        throw new IllegalArgumentException("Service name must exist");
    }

    @RequestMapping(value = "/{" + CONTAINER_NAME_VARIABLE_NAME + "}/read", method = RequestMethod.GET)
    public ResponseEntity<String> readSource(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName,
                                             @RequestParam("class") String className) {
        return agentContainerManager.findAgentContainer(containerName)
                .map(container -> {
                    try {
                        return JarReader.ofContainer(container,
                                reader -> readSourceFromJar(reader, containerName, className));
                    } catch (IOException e) {
                        LOGGER.warn("Failed to read from container file {}: " + containerName, e);
                        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Failed to open container of name " + containerName);
                    }
                })
                .orElse(ResponseEntity.badRequest().body("No container deployed under the name " + containerName));
    }

    public void setAgentContainerManager(AgentContainerManager agentContainerManager) {
        this.agentContainerManager = agentContainerManager;
    }

    public void setAgentRegistrySpawner(AgentRegistrySpawner agentRegistrySpawner) {
        this.agentRegistrySpawner = agentRegistrySpawner;
    }

    public void setAgentRunnerSpawner(AgentRunnerSpawner agentRunnerSpawner) {
        this.agentRunnerSpawner = agentRunnerSpawner;
    }

    public void setJarWriter(JarWriter jarWriter) {
        this.jarWriter = jarWriter;
    }

    /**
     * Attempts to read the given class name from a jar file using the given {@link JarReader}.
     *
     * @param reader        The jar reader which can open a jar for reading.
     * @param containerName The name of the container used for error messages.
     * @param className     The full name and path of the class, but without any suffixes.
     *                      Example: <code>org.test.ClassName</code>
     * @return A HTTP {@link ResponseEntity} with a string on success or an error on failure.
     */
    private static ResponseEntity<String> readSourceFromJar(JarReader reader, String containerName, String className) {
        final String entry = className.replace(".", "/") + JAVA_CLASS_SUFFIX;
        try {
            return ResponseEntity.ok(reader.readEntry(entry));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest()
                    .body("No class source found for entry " + entry);
        } catch (IOException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("Failed to read entry " + entry + " inside container " + containerName);
        }
    }

}
