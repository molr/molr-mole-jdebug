package cern.molr.controller.server;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.commons.domain.Service;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.entry.EntryListener;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author tiagomr
 */
public interface Controller {

    /**
     * Deploys an MoleContainer and start its discovery service
     *
     * @param containerName The name of the deployment
     * @param file          The bytes from the MoleContainer file
     */
    void deploy(String containerName, byte[] file) throws Exception;

    void deploy(MoleContainer container);

    /**
     * Registers a specific container as available
     *
     * @param moleContainer The {@link }MoleContainer} to be registered
     */
    void registerService(@RequestBody MoleContainer moleContainer);

    /**
     * Lists all registered {@link MoleContainer}s
     *
     * @return A {@link List} with the registered {@link MoleContainer}s
     */
    Set<MoleContainer> getAllContainers();

    /**
     * @return An {@link Optional} of {@link MoleContainer} if it is registered, empty otherwise
     */
    Optional<MoleContainer> getContainer(String containerName);

    /**
     * Executes a {@link Service} from a specific {@link MoleContainer}
     *
     * @param agentPath   {@link String} with the path to the {@link MoleContainer} from where the service will be executed
     * @param service     {@link Service} to be executed
     * @param entryPoints {@link List} of entry points to be executed, all entry points are executed if no
     *                    {@link List} is provided
     * @return A {@link String} with the result of the execution
     */
    String runMole(String agentPath, Service service, List<String> entryPoints) throws Exception;

    JdiController debugMole(String agentPath, Service service, List<String> entryPoints, EntryListener entryListener) throws Exception;

    /**
     * Fetches the source code of a {@link Class} from a specific {@link MoleContainer}
     *
     * @param moleContainer {@link MoleContainer} from where the source code will be fetched
     * @param className      {@link String} with the fully cqualified name to the class
     * @return A {@link String} with the source code
     */
    String readSource(MoleContainer moleContainer, String className) throws IOException;
}