/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller.rest.controller;

import cern.molr.commons.domain.Mission;
import cern.molr.controller.server.Controller;
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
 * {@link RestController} that acts as a facade to access {@link Controller} functionalities
 *
 * @author tiagomr
 */
@RestController
@RequestMapping("${rest.basepath}/container")
public class AgentContainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentContainerController.class);
    private static final String CONTAINER_NAME_VARIABLE_NAME = "containerName";

    @Autowired
    private Controller controller;

    @RequestMapping(value = "/deploy/{" + CONTAINER_NAME_VARIABLE_NAME + "}", method = RequestMethod.POST)
    public void deploy(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName, @RequestBody byte[] jar) throws Exception {
        controller.deploy(containerName, jar);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerService(@RequestBody MoleContainer moleContainer) {
        controller.registerService(moleContainer);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<MoleContainer> listContainers() {
        return controller.getAllContainers();
    }

    @RequestMapping(value = "/{" + CONTAINER_NAME_VARIABLE_NAME + "}/start", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String runService(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName,
                             @RequestParam(value = "service") String serviceName,
                             @RequestParam(value = "entryPoints", defaultValue = "") String entryPoints) throws Exception {
        final MoleContainer moleContainer = controller.getContainer(containerName).orElseThrow(() -> new IllegalArgumentException("Provided container name not found"));
        final Mission mission = moleContainer.getMissions().stream()
                .filter(serviceToFilter -> serviceName.equals(serviceToFilter.getMissionContentClassName()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Provided mission name not found"));

        final List<String> parsedEntryPoints = (List<String>) Arrays.asList(entryPoints.split(",")).stream()
                .filter(((Predicate<String>) String::isEmpty).negate())
                .collect(Collectors.toList());

        parsedEntryPoints.forEach(entryPoint -> {
            if (!mission.getTasksNames().contains(entryPoint)) {
                throw new IllegalArgumentException("All entry points must exist");
            }
        });

        return controller.runMole(moleContainer.getContainerPath(), mission, parsedEntryPoints);
    }

    @RequestMapping(value = "/{" + CONTAINER_NAME_VARIABLE_NAME + "}/read", method = RequestMethod.GET)
    public String readSource(@PathVariable(CONTAINER_NAME_VARIABLE_NAME) String containerName,
                             @RequestParam("class") String className) throws IOException {
        final MoleContainer moleContainer = controller.getContainer(containerName).orElseThrow(() -> new IllegalArgumentException("Provided container name not found"));
        return controller.readSource(moleContainer, className.replace(".", "/"));
    }
}
