package cern.molr.controller.server.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.domain.MoleContainer;
import cern.molr.controller.io.JarReader;
import cern.molr.controller.io.JarWriter;
import cern.molr.controller.manager.MoleManager;
import cern.molr.controller.server.Controller;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.entry.EntryListener;
import cern.molr.jvm.MoleSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * @author tiagomr
 */
public class ControllerImpl implements Controller {

    // TODO: 19/02/2016 Finish this class

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerImpl.class);
    private static final File DEPLOYMENT_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String CONTAINER_NAME_VARIABLE_NAME = "containerName";
    static final String JAVA_CLASS_SUFFIX = ".java";

    @Autowired
    private MoleManager moleManager;
    @Autowired
    private MoleSpawner<String> moleRegistrySpawner;
    @Autowired
    private MoleSpawner moleRunnerSpawner;
    @Autowired
    private JarWriter jarWriter;

    @Override
    public void deploy(String containerName, byte[] file) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerService(@RequestBody MoleContainer moleContainer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<MoleContainer> getAllContainers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<MoleContainer> getContainer(String moleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String runMole(String agentPath, Mission mission, String... tasks) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public JdiController debugMole(String agentPath, Mission mission, EntryListener entryListener, String... tasks) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String readSource(MoleContainer moleContainer, String className) throws IOException {
        throw new UnsupportedOperationException();
    }

    private Optional<String> readSource(JarReader reader, String className) {
        throw new UnsupportedOperationException();
    }

}