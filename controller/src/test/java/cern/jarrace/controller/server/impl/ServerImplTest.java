package cern.jarrace.controller.server.impl;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.controller.io.JarReader;
import cern.jarrace.controller.io.JarWriter;
import cern.jarrace.controller.jvm.AgentRegistrySpawner;
import cern.jarrace.controller.jvm.AgentRunnerSpawner;
import cern.jarrace.controller.manager.AgentContainerManager;
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
public class ServerImplTest {

    private static final String TEST_CONTAINER_NAME = "TEST_CONTAINER_NAME";
    private static final byte[] TEST_CONTAINER_BYTES = "TEST_CONTAINER_BYTES".getBytes();
    private static final String TEST_CONTAINER_PATH = "TEST_CONTAINER_PATH";
    @Mock
    private AgentContainerManager agentContainerManager;
    @Mock
    private AgentRegistrySpawner agentRegistrySpawner;
    @Mock
    private AgentRunnerSpawner agentRunnerSpawner;
    @Mock
    private JarWriter jarWriter;
    @Mock
    private JarReader jarReader;
    @InjectMocks
    private ServerImpl serverImpl;

    @Test
    public void testDeploy() throws Exception {
        when(jarWriter.writeFile(TEST_CONTAINER_NAME, TEST_CONTAINER_BYTES)).thenReturn(TEST_CONTAINER_PATH);
        serverImpl.deploy(TEST_CONTAINER_NAME, TEST_CONTAINER_BYTES);
        verify(jarWriter).writeFile(TEST_CONTAINER_NAME, TEST_CONTAINER_BYTES);
        verify(agentRegistrySpawner).spawnAgentRegistry(TEST_CONTAINER_NAME, TEST_CONTAINER_PATH);
    }

    @Test
    public void testRegister() throws Exception {
        AgentContainer agentContainer = mock(AgentContainer.class);
        serverImpl.registerService(agentContainer);
        verify(agentContainerManager).registerAgentContainer(agentContainer);
    }

    @Test
    public void testGetAllContainers() {
        Set<AgentContainer> agentContainers = mock(Set.class);
        when(agentContainerManager.findAllAgentContainers()).thenReturn(agentContainers);
        assertEquals(agentContainers, serverImpl.getAllContainers());
    }

    @Test
    public void testGetContainer() {
        AgentContainer agentContainer = mock(AgentContainer.class);
        Optional<AgentContainer> optionalAgentContainer = Optional.of(agentContainer);
        when(agentContainerManager.findAgentContainer(TEST_CONTAINER_NAME)).thenReturn(optionalAgentContainer);
        assertEquals(agentContainer, serverImpl.getContainer(TEST_CONTAINER_NAME).get());
        verify(agentContainerManager).findAgentContainer(TEST_CONTAINER_NAME);
    }

    @Test
    public void testRunService() throws Exception {
        Service service = mock(Service.class);
        List<String> stringList = mock(List.class);
        serverImpl.runService(TEST_CONTAINER_PATH, service, stringList);
        verify(agentRunnerSpawner).spawnAgentRunner(service, TEST_CONTAINER_PATH, stringList);
    }
}