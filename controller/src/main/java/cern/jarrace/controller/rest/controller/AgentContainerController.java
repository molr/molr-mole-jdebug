package cern.jarrace.controller.rest.controller;

import cern.jarrace.controller.domain.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author timartin
 * Controller that exposes services to manage {@link Service}s
 */
@RestController
@RequestMapping("/jarrace")
public class AgentContainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentContainerController.class);
    private static final File DEPLOYMENT_DIR = new File(System.getProperty("java.io.tmpdir"));

    final Map<String, List<Service>> entrypoints = new HashMap<>();
    final Map<String, String> paths = new HashMap<>();

    @RequestMapping(value = "/container/deploy/{name}", method = RequestMethod.POST)
    public void deploy(@PathVariable("name") String name, @RequestBody byte[] jar) throws IOException {
        System.out.println("Deployed " + name);
        String path = writeFile(name, jar);
        paths.put(name, path);
        entrypoints.put(name, new ArrayList<>());
        List<String> args = new ArrayList<>();
        args.add("cern.jarrace.agent.AgentContainer");
        args.add(name);
        args.add("localhost:8080");
        startContainer(path, args);
    }

    @RequestMapping(value = "/{containerName}/service/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerService(@PathVariable(value = "containerName") String containerName, @RequestBody Service service){
        if(service.getAgentName() == null || service.getAgentName().isEmpty()) {
            throw new IllegalArgumentException("Agent name must be different than null and not empty");
        }
        if(service.getClazz() == null || service.getClazz().isEmpty()) {
            throw new IllegalArgumentException("Clazz must be different than null and not empty");
        }
        if(service.getEntrypoints() == null || service.getEntrypoints().size() == 0) {
            throw new IllegalArgumentException("Endpoints must be different than null and not empty");
        }
        if(containerName == null || entrypoints.get(containerName) == null) {
            throw new IllegalArgumentException("Provided container name does not exists.");
        }
        entrypoints.get(containerName).add(service);
        LOGGER.info(entrypoints.toString());
    }

    @RequestMapping(value = "/container/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> listContainers() {
        return entrypoints.keySet();
    }

    @RequestMapping(value = "/{containerName}/service/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Service> listServices(@PathVariable(value = "containerName") String containerName) {
        List<Service> toReturn = this.entrypoints.get(containerName);
        if (toReturn == null) {
            toReturn = new ArrayList<>();
        }
        return toReturn;
    }

    @RequestMapping(value = "/{name}/{entrypoint}/start", method = RequestMethod.GET)
    public String runService(@PathVariable("name") String name, @PathVariable int entrypoint) throws IOException {
        List<Service> entries = entrypoints.get(name);
        Service entryPoint = entries.get(entrypoint);
        List<String> args = new ArrayList<>();
        args.add("cern.jarrace.agent.AgentRunner");
        args.add(entryPoint.getAgentName());
        args.add(entryPoint.getClazz());
        args.add(entryPoint.getEntrypoints().stream().collect(Collectors.joining(",")));
        startContainer(paths.get(name), args);
        return null;
    }

    private void startContainer(String path, List<String> args) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(String.format("%s/bin/java", System.getProperty("java.home")));
        command.add("-cp");
        command.add(path);
        command.addAll(args);

        LOGGER.info(String.format("Starting agent container [%s]", command.toString()));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO().start();
    }

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
