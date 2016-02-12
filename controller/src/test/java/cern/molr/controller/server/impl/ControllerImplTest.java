package cern.molr.controller.server.impl;

import cern.molr.commons.domain.Mole;
import cern.molr.commons.domain.Service;
import cern.molr.controller.io.JarReader;
import cern.molr.controller.io.JarWriter;
import cern.molr.controller.jvm.MoleRegistrySpawner;
import cern.molr.controller.jvm.MoleRunnerSpawner;
import cern.molr.controller.manager.MoleManager;
import cern.molr.controller.server.impl.ControllerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class ControllerImplTest {

    private static final String TEST_CONTAINER_NAME = "TEST_CONTAINER_NAME";
    private static final byte[] TEST_CONTAINER_BYTES = "TEST_CONTAINER_BYTES".getBytes();
    private static final String TEST_CONTAINER_PATH = "TEST_CONTAINER_PATH";
    @Mock
    private MoleManager moleManager;
    @Mock
    private MoleRegistrySpawner moleRegistrySpawner;
    @Mock
    private MoleRunnerSpawner moleRunnerSpawner;
    @Mock
    private JarWriter jarWriter;
    @Mock
    private JarReader jarReader;
    @InjectMocks
    private ControllerImpl serverImpl;

    @Test
    public void testDeploy() throws Exception {
        when(jarWriter.writeFile(TEST_CONTAINER_NAME, TEST_CONTAINER_BYTES)).thenReturn(TEST_CONTAINER_PATH);
        serverImpl.deploy(TEST_CONTAINER_NAME, TEST_CONTAINER_BYTES);
        verify(jarWriter).writeFile(TEST_CONTAINER_NAME, TEST_CONTAINER_BYTES);
        verify(moleRegistrySpawner).spawnAgentRegistry(TEST_CONTAINER_NAME, TEST_CONTAINER_PATH);
    }

    @Test
    public void testRegister() throws Exception {
        Mole mole = mock(Mole.class);
        serverImpl.registerService(mole);
        verify(moleManager).registerMole(mole);
    }

    @Test
    public void testGetAllContainers() {
        Set<Mole> moles = mock(Set.class);
        when(moleManager.getAllMoles()).thenReturn(moles);
        assertEquals(moles, serverImpl.getAllContainers());
    }

    @Test
    public void testGetContainer() {
        Mole mole = mock(Mole.class);
        Optional<Mole> optionalAgentContainer = Optional.of(mole);
        when(moleManager.getMole(TEST_CONTAINER_NAME)).thenReturn(optionalAgentContainer);
        assertEquals(mole, serverImpl.getContainer(TEST_CONTAINER_NAME).get());
        verify(moleManager).getMole(TEST_CONTAINER_NAME);
    }

    @Test
    public void testRunService() throws Exception {
        Service service = mock(Service.class);
        List<String> stringList = mock(List.class);
        serverImpl.runMole(TEST_CONTAINER_PATH, service, stringList);
        verify(moleRunnerSpawner).spawnAgentRunner(service, TEST_CONTAINER_PATH, stringList);
    }
}