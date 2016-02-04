/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.jarrace.controller.rest.controller;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link RestController} that acts as a facade to access {@link cern.jarrace.controller.server.Server} functionalities
 *
 * @author tiagomr
 */
@RestController
@RequestMapping("${rest.basepath}/container")
public class AgentContainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentContainerController.class);
    private static final String CONTAINER_NAME_VARIABLE_NAME = "containerName";

    @Autowired
    private Server server;


    @RequestMapping(value = "/deploy/{" + CONTAINER_NAME_VARIABLE_NAME + "}", method = RequestMethod.POST)
    public void deploy(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName, @RequestBody byte[] jar) throws Exception {
        server.deploy(containerName, jar);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerService(@RequestBody AgentContainer agentContainer) {
        server.registerService(agentContainer);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<AgentContainer> listContainers() {
        return server.getAllContainers();
    }

    @RequestMapping(value = "/{" + CONTAINER_NAME_VARIABLE_NAME + "}/start", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String runService(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName,
                             @RequestParam(value = "service") String serviceName,
                             @RequestParam(value = "entryPoints", defaultValue = "") String entryPoints) throws Exception {

        final AgentContainer agentContainer = server.getContainer(containerName).orElseThrow(() -> new IllegalArgumentException("Provided container name not found"));
        final Service service = agentContainer.getServices().stream()
                .filter(serviceToFilter -> serviceName.equals(serviceToFilter.getClassName()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Provided container name not found"));

        final List<String> parsedEntryPoints = (List<String>) Arrays.asList(entryPoints.split(",")).stream()
                .filter(((Predicate<String>) String::isEmpty).negate())
                .collect(Collectors.toList());

        parsedEntryPoints.forEach(entryPoint -> {
            if (!service.getEntryPoints().contains(entryPoint)) {
                throw new IllegalArgumentException("All entry points must exist");
            }
        });

        return server.runService(agentContainer.getContainerPath(), service, parsedEntryPoints);
    }

    @RequestMapping(value = "/{" + CONTAINER_NAME_VARIABLE_NAME + "}/read", method = RequestMethod.GET)
    public String readSource(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName,
                             @RequestParam("class") String className) throws IOException {
        final AgentContainer agentContainer = server.getContainer(containerName).orElseThrow(() -> new IllegalArgumentException("Provided container name not found"));
        return server.readSource(agentContainer, className.replace(".", "/"));
    }
}
