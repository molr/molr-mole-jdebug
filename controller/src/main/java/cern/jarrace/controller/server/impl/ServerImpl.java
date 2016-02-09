package cern.jarrace.controller.server.impl;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.commons.instantiation.InstantiationRequest;
import cern.jarrace.controller.io.JarReader;
import cern.jarrace.controller.io.JarWriter;
import cern.jarrace.controller.jvm.AgentRegistrySpawner;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import cern.jarrace.controller.manager.AgentContainerManager;
import cern.jarrace.controller.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author tiagomr
 */
public class ServerImpl implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerImpl.class);
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

    @Override
    public void deploy(String containerName, byte[] file) throws Exception {
        LOGGER.debug("Started deployment process for container: [{}]", containerName);
        String path = jarWriter.writeFile(containerName, file);
        agentRegistrySpawner.spawnAgentRegistry(containerName, path);
    }

    @Override
    public void deploy(AgentContainer container) {
        agentContainerManager.registerAgentContainer(container);
    }

    @Override
    public void registerService(@RequestBody AgentContainer agentContainer) {
        agentContainerManager.registerAgentContainer(agentContainer);
        LOGGER.info("Registered new AgentContainer: [{}]", agentContainer);
    }

    @Override
    public Set<AgentContainer> getAllContainers() {
        return agentContainerManager.findAllAgentContainers();
    }

    @Override
    public Optional<AgentContainer> getContainer(String containerName) {
        return agentContainerManager.findAgentContainer(containerName);
    }

    @Override
    public String runService(String agentPath, Service service, List<String> entryPoints) throws Exception {
        return agentRunnerSpawner.spawnAgentRunner(service, agentPath, entryPoints);
    }

    @Override
    public String readSource(AgentContainer agentContainer, String className) throws IOException {
        return JarReader.ofContainer(agentContainer, reader -> readSource(reader, className))
                .orElseThrow(() -> new IOException("Failed to read source file from jar"));
    }

    private Optional<String> readSource(JarReader reader, String className) {
        try {
            return Optional.of(reader.readEntry(className + JAVA_CLASS_SUFFIX));
        } catch (IOException e) {
            LOGGER.warn("Failed to read entry from Jar");
            return Optional.empty();
        }
    }

}