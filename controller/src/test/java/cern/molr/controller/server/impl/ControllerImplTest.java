package cern.molr.controller.server.impl;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.commons.domain.Service;
import cern.molr.controller.io.JarReader;
import cern.molr.controller.io.JarWriter;
import cern.molr.jvm.MoleSpawner;
import cern.molr.controller.manager.MoleManager;
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
    private MoleSpawner moleRunnerSpawner;
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
        MoleContainer moleContainer = mock(MoleContainer.class);
        serverImpl.registerService(moleContainer);
        verify(moleManager).registerMole(moleContainer);
    }

    @Test
    public void testGetAllContainers() {
        Set<MoleContainer> moleContainers = mock(Set.class);
        when(moleManager.getAllMoles()).thenReturn(moleContainers);
        assertEquals(moleContainers, serverImpl.getAllContainers());
    }

    @Test
    public void testGetContainer() {
        MoleContainer moleContainer = mock(MoleContainer.class);
        Optional<MoleContainer> optionalAgentContainer = Optional.of(moleContainer);
        when(moleManager.getMole(TEST_CONTAINER_NAME)).thenReturn(optionalAgentContainer);
        assertEquals(moleContainer, serverImpl.getContainer(TEST_CONTAINER_NAME).get());
        verify(moleManager).getMole(TEST_CONTAINER_NAME);
    }

    @Test
    public void testRunService() throws Exception {
        Service service = mock(Service.class);
        List<String> stringList = mock(List.class);
        serverImpl.runMole(TEST_CONTAINER_PATH, service, stringList);
        verify(moleRunnerSpawner).spawnMoleRunner(service, TEST_CONTAINER_PATH, stringList);
    }
}