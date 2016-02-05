package cern.jarrace.controller.server;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author tiagomr
 */
public interface Server {

    /**
     * Deploys an AgentContainer and start its discovery service
     *
     * @param containerName The name of the deployment
     * @param file          The bytes from the AgentContainer file
     */
    void deploy(String containerName, byte[] file) throws Exception;

    /**
     * Registers a specific container as available
     *
     * @param agentContainer The {@link }AgentContainer} to be registered
     */
    void registerService(@RequestBody AgentContainer agentContainer);

    /**
     * Lists all registered {@link AgentContainer}s
     *
     * @return A {@link List} with the registered {@link AgentContainer}s
     */
    Set<AgentContainer> getAllContainers();

    /**
     * @return An {@link Optional} of {@link AgentContainer} if it is registered, empty otherwise
     */
    Optional<AgentContainer> getContainer(String containerName);

    /**
     * Executes a {@link Service} from a specific {@link AgentContainer}
     *
     * @param agentPath   {@link String} with the path to the {@link AgentContainer} from where the service will be executed
     * @param service     {@link Service} to be executed
     * @param entryPoints {@link List} of entry points to be executed, all entry points are executed if no
     *                    {@link List} is provided
     * @return A {@link String} with the result of the execution
     */
    String runService(String agentPath, Service service, List<String> entryPoints) throws Exception;

    /**
     * Fetches the source code of a {@link Class} from a specific {@link AgentContainer}
     *
     * @param agentContainer {@link AgentContainer} from where the source code will be fetched
     * @param className      {@link String} with the fully cqualified name to the class
     * @return A {@link String} with the source code
     */
    String readSource(AgentContainer agentContainer, String className) throws IOException;
}