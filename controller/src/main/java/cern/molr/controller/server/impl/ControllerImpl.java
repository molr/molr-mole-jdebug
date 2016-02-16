package cern.molr.controller.server.impl;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.commons.domain.Service;
import cern.molr.controller.io.JarReader;
import cern.molr.controller.io.JarWriter;
import cern.molr.jvm.MoleRunnerSpawner;
import cern.molr.controller.manager.MoleManager;
import cern.molr.controller.server.Controller;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.controller.JdiControllerImpl;
import cern.molr.inspector.entry.EntryListener;
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
public class ControllerImpl implements Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerImpl.class);
    private static final File DEPLOYMENT_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String CONTAINER_NAME_VARIABLE_NAME = "containerName";
    static final String JAVA_CLASS_SUFFIX = ".java";

    @Autowired
    private MoleManager moleManager;
    @Autowired
    private MoleRegistrySpawner moleRegistrySpawner;
    @Autowired
    private MoleRunnerSpawner moleRunnerSpawner;
    @Autowired
    private JarWriter jarWriter;

    @Override
    public void deploy(String containerName, byte[] file) throws Exception {
        LOGGER.debug("Started deployment process for container: [{}]", containerName);
        String path = jarWriter.writeFile(containerName, file);
        moleRegistrySpawner.spawnAgentRegistry(containerName, path);
    }

    @Override
    public void registerService(@RequestBody MoleContainer moleContainer) {
        moleManager.registerMole(moleContainer);
        LOGGER.info("Registered new MoleContainer: [{}]", moleContainer);
    }

    @Override
    public Set<MoleContainer> getAllContainers() {
        return moleManager.getAllMoles();
    }

    @Override
    public Optional<MoleContainer> getContainer(String moleName) {
        return moleManager.getMole(moleName);
    }

    @Override
    public String runMole(String agentPath, Service service, List<String> entryPoints) throws Exception {
        return moleRunnerSpawner.spawnAgentRunner(service, agentPath, entryPoints);
    }

    @Override
    public JdiController debugMole(String agentPath, Service service, List<String> entryPoints, EntryListener entryListener) throws Exception {
        return JdiControllerImpl.builder()
                .setClassPath(agentPath)
                .setListenerFactory((x, y) -> entryListener)
                .setService(service)
                .build();
    }

    @Override
    public String readSource(MoleContainer moleContainer, String className) throws IOException {
        return JarReader.ofContainer(moleContainer, reader -> readSource(reader, className))
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