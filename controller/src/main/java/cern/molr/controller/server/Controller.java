package cern.molr.controller.server;

import cern.molr.commons.domain.Mole;
import cern.molr.commons.domain.Service;
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
     * Deploys an Mole and start its discovery service
     *
     * @param containerName The name of the deployment
     * @param file          The bytes from the Mole file
     */
    void deploy(String containerName, byte[] file) throws Exception;

    void deploy(Mole container);

    /**
     * Registers a specific container as available
     *
     * @param mole The {@link }Mole} to be registered
     */
    void registerService(@RequestBody Mole mole);

    /**
     * Lists all registered {@link Mole}s
     *
     * @return A {@link List} with the registered {@link Mole}s
     */
    Set<Mole> getAllContainers();

    /**
     * @return An {@link Optional} of {@link Mole} if it is registered, empty otherwise
     */
    Optional<Mole> getContainer(String containerName);

    /**
     * Executes a {@link Service} from a specific {@link Mole}
     *
     * @param agentPath   {@link String} with the path to the {@link Mole} from where the service will be executed
     * @param service     {@link Service} to be executed
     * @param entryPoints {@link List} of entry points to be executed, all entry points are executed if no
     *                    {@link List} is provided
     * @return A {@link String} with the result of the execution
     */
    String runMole(String agentPath, Service service, List<String> entryPoints) throws Exception;

    /**
     * Fetches the source code of a {@link Class} from a specific {@link Mole}
     *
     * @param mole {@link Mole} from where the source code will be fetched
     * @param className      {@link String} with the fully cqualified name to the class
     * @return A {@link String} with the source code
     */
    String readSource(Mole mole, String className) throws IOException;
}